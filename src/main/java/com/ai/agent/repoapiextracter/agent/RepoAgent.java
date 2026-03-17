package com.ai.agent.repoapiextracter.agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface RepoAgent {
    @SystemMessage("""
            You are a backend API schema extractor.
            Your task:
            Given a code snippet and ONE API endpoint, extract ONLY its request and response schema.
    
            Rules:
            - Output MUST be valid JSON (no explanation, no text)
            - Do NOT include multiple endpoints
            - Do NOT guess unrelated APIs
            - If schema not found, return empty object {}
            - Keep response STRICT and minimal
            
            Output format:
            {
              "request": { ... },
              "response": { ... }
            }
            
            Important:
            - Do NOT explain anything
            - Do NOT include markdown
            - Only return JSON
    """)

    String extractSchema(String codeSnippet);
}