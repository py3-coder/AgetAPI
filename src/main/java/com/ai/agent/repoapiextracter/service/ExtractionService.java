package com.ai.agent.repoapiextracter.service;

import com.ai.agent.repoapiextracter.agent.RepoTools;
import com.ai.agent.repoapiextracter.model.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ExtractionService {

    private final GitService gitService;
    private final RepoScannerService scanner;
    private final EndpointExtractorService extractor;
    private final CodeSnippetExtractorService snippetService;
    private final SchemaGeneratorService schemaService;
    private final RepoTools repoTools;

    public ExtractionService(GitService gitService,
                             RepoScannerService scanner,
                             EndpointExtractorService extractor,
                             CodeSnippetExtractorService snippetService,
                             SchemaGeneratorService schemaService,
                             RepoTools repoTools) {

        this.gitService = gitService;
        this.scanner = scanner;
        this.extractor = extractor;
        this.snippetService = snippetService;
        this.schemaService = schemaService;
        this.repoTools = repoTools;
    }

    public RepositoryStructure extract(String repoUrl) throws Exception {

        // 1. Clone repo
        Path repo = gitService.cloneRepo(repoUrl);
        System.out.println("Repo cloned at: " + repo);
        repoTools.setRootPath(repo);

        // 2. Scan files
        List<Path> files = scanner.findApiFiles(repo);
        System.out.println("Total files found: " + files.size());

        // 3. Extract endpoints
        List<ApiEndpoint> rawEndpoints = new ArrayList<>();
        for (Path file : files) {
            rawEndpoints.addAll(extractor.extractEndpoints(file));
        }

        // 4. REMOVE DUPLICATES (VERY IMPORTANT)
        Map<String, ApiEndpoint> uniqueMap = new LinkedHashMap<>();

        for (ApiEndpoint ep : rawEndpoints) {
            String key = ep.getMethod() + ":" + ep.getPath();
            uniqueMap.putIfAbsent(key, ep);
        }

        List<ApiEndpoint> endpoints = new ArrayList<>(uniqueMap.values());

        System.out.println("Unique endpoints: " + endpoints.size());

        // 5. LLM SCHEMA GENERATION (CONTROLLED + SAFE)
        // limit total endpoints (avoid quota burn)
        int LIMIT = 5;

        //max concurrent LLM calls
        int THREADS = 2;
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<?>> futures = new ArrayList<>();
        List<ApiEndpoint> limitedEndpoints = endpoints.stream()
                        .limit(LIMIT)
                        .toList();
        for (ApiEndpoint ep : limitedEndpoints) {
            Future<?> future = executor.submit(() -> {
                try {
                    String snippet = snippetService.extractSnippet(
                            Path.of(ep.getSourceFile()),
                            ep.getPath()
                    );

                    //  skip useless content
                    if (snippet == null || snippet.length() < 50) return;
                    String input = """ 
                            Endpoint: %s %s
                            Extract schema ONLY for this endpoint.
                            Code:
                            %s
                            """.formatted(ep.getMethod(), ep.getPath(), snippet);
                    String schema = schemaService.generateSchema(input);
                    if (!schema.trim().startsWith("{")) {
                        System.out.println("Invalid schema, skipping...");
                        return;
                    }
                    if (schema == null || schema.isBlank()) {
                        schema = inferBasicSchema(snippet);
                    }
                    ep.setRequestSchema(schema);
                    //  OPTIONAL: small delay to avoid rate limit (very useful)
                    Thread.sleep(250);
                } catch (Exception e) {
                    System.out.println("Schema failed for: " + ep.getPath());
                }
            });
            futures.add(future);
        }

        // wait for all tasks to complete
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception ignored) {}
        }
        executor.shutdown();

        // 6. Build response
        RepositoryStructure rs = new RepositoryStructure();
        rs.setEndpoints(endpoints);
        return rs;
    }
    private String inferBasicSchema(String code) {
        Map<String, Object> request = new HashMap<>();
        if (code.contains("req.body")) {
            request.put("body", "object");
        }
        if (code.contains("req.params")) {
            request.put("params", "object");
        }
        if (code.contains("req.query")) {
            request.put("query", "object");
        }
        if (code.contains("req.file") || code.contains("multer")) {
            request.put("file", "binary");
        }
        return "{ \"request\": " + request.toString() + ", \"response\": {} }";
    }
}