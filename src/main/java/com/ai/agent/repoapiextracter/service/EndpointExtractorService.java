package com.ai.agent.repoapiextracter.service;
import com.ai.agent.repoapiextracter.model.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.*;

@Service
public class EndpointExtractorService {

    // Matches: router.get("/path") OR app.post('/path')
    private static final Pattern DIRECT_PATTERN =
            Pattern.compile("\\.(get|post|put|delete|patch)\\(['\"]([^'\"]+)['\"]");

    // Matches: router.route("/path").get()
    private static final Pattern ROUTE_CHAIN_PATTERN =
            Pattern.compile("route\\(['\"]([^'\"]+)['\"]\\)\\.(get|post|put|delete)");

    // @GetMapping("/users")
    private static final Pattern SPRING_MAPPING =
            Pattern.compile("@(Get|Post|Put|Delete|Patch)Mapping\\(\"([^\"]+)\"\\)");

    // @RequestMapping(value="/users", method=RequestMethod.GET)
    private static final Pattern SPRING_REQUEST_MAPPING =
            Pattern.compile("@RequestMapping\\(.*value\\s*=\\s*\"([^\"]+)\".*method\\s*=\\s*RequestMethod\\.(GET|POST|PUT|DELETE)");
    // @RequestMapping Class Level Path
    Pattern CLASS_MAPPING =
            Pattern.compile("@RequestMapping\\(\"([^\"]+)\"\\)");

    public List<ApiEndpoint> extractEndpoints(Path file) {

        List<ApiEndpoint> endpoints = new ArrayList<>();

        try {
            String filePath = file.toString();
            // Ignore unwanted folders
            if (filePath.contains("node_modules") ||
                    filePath.contains("test") ||
                    filePath.contains("static") ||
                    filePath.contains("dist")) {
                return endpoints;
            }
            String content = Files.readString(file);

            // Use Set for DISTINCT APIs
            Set<String> uniqueEndpoints = new HashSet<>();

            // ===== CASE 1: router.get("/path") =====
            Matcher m1 = DIRECT_PATTERN.matcher(content);
            while (m1.find()) {

                String method = m1.group(1).toUpperCase();
                String path = m1.group(2);

                // FILTER: only valid API paths
                if (!isValidPath(path)) continue;

                String key = method + ":" + path;

                if (!uniqueEndpoints.contains(key)) {
                    uniqueEndpoints.add(key);

                    ApiEndpoint ep = new ApiEndpoint();
                    ep.setMethod(method);
                    ep.setPath(path);
                    ep.setSourceFile(filePath);

                    endpoints.add(ep);
                }
            }

            // ===== CASE 2: router.route("/path").get() =====
            Matcher m2 = ROUTE_CHAIN_PATTERN.matcher(content);
            while (m2.find()) {

                String path = m2.group(1);
                String method = m2.group(2).toUpperCase();

                if (!isValidPath(path)) continue;

                String key = method + ":" + path;

                if (!uniqueEndpoints.contains(key)) {
                    uniqueEndpoints.add(key);

                    ApiEndpoint ep = new ApiEndpoint();
                    ep.setMethod(method);
                    ep.setPath(path);
                    ep.setSourceFile(filePath);

                    endpoints.add(ep);
                }
            }
            // ===== CASE 3: @GetMapping("/users") =====
            Matcher m3 = SPRING_MAPPING.matcher(content);

            while (m3.find()) {

                String method = m3.group(1).toUpperCase();
                String path = m3.group(2);

                if (!isValidPath(path)) continue;

                String key = method + ":" + path;

                if (!uniqueEndpoints.contains(key)) {
                    uniqueEndpoints.add(key);

                    ApiEndpoint ep = new ApiEndpoint();
                    ep.setMethod(method);
                    ep.setPath(path);
                    ep.setSourceFile(filePath);

                    endpoints.add(ep);
                }
            }
            // ===== CASE 4: @RequestMapping =====
            Matcher m4 = SPRING_REQUEST_MAPPING.matcher(content);

            while (m4.find()) {

                String path = m4.group(1);
                String method = m4.group(2).toUpperCase();

                if (!isValidPath(path)) continue;

                String key = method + ":" + path;

                if (!uniqueEndpoints.contains(key)) {
                    uniqueEndpoints.add(key);

                    ApiEndpoint ep = new ApiEndpoint();
                    ep.setMethod(method);
                    ep.setPath(path);
                    ep.setSourceFile(filePath);

                    endpoints.add(ep);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return endpoints;
    }

    // Validation
    private boolean isValidPath(String path) {

        if (path == null || path.isEmpty()) return false;

        // Must start with /
        if (!path.startsWith("/")) return false;

        // Avoid config-like values
        if (path.contains("server.") ||
                path.contains("config") ||
                path.contains("env")) return false;

        return true;
    }
}