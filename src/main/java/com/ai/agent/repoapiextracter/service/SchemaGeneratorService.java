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

            return repoAgent.extractSchema(snippet);

        } catch (Exception e) {
            throw new RuntimeException("Schema extraction failed", e);
        }
    }
}