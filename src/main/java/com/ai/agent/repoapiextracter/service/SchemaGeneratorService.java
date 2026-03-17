package com.ai.agent.repoapiextracter.service;


import com.ai.agent.repoapiextracter.agent.RepoAgent;
import org.springframework.stereotype.Service;

@Service
public class SchemaGeneratorService {

    private final RepoAgent repoAgent;

    public SchemaGeneratorService(RepoAgent repoAgent) {
        this.repoAgent = repoAgent;
    }

    public String generateSchema(String snippet) {
        try {
            if (snippet == null || snippet.isEmpty()) {
                return null;
            }

            String schemaResponse = repoAgent.extractSchema(snippet);
            return cleanJson(schemaResponse);

        } catch (Exception e) {
            throw new RuntimeException("Schema extraction failed", e);
        }
    }
    private String cleanJson(String response) {
        if (response == null) return null;
        response = response.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start != -1 && end != -1) {
            return response.substring(start, end + 1);
        }

        return null;
    }
}