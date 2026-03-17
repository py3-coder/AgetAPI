package com.ai.agent.repoapiextracter.agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface RepoAgent {

    @SystemMessage("""
    You are an API analyzer.

    Given code, extract:
    1. Request JSON schema
    2. Response JSON schema

    Return JSON only:
    {
      "request": {...},
      "response": {...}
    }
    """)

    String extractSchema(String codeSnippet);
}