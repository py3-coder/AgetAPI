package com.ai.agent.repoapiextracter.config;

import com.ai.agent.repoapiextracter.agent.RepoAgent;
import com.ai.agent.repoapiextracter.agent.RepoTools;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.modelName}")
    private String modelName;

    @Value("${groq.api.baseUrl}")
    private String baseUrl;

    @Bean
    public OpenAiChatModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.2)
                .build();
    }
}