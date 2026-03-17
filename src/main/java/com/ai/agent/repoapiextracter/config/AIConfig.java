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

//    @Value("${langchain4j.google-ai.api-key}")
//    private String apiKey;
//
//    @Value("${langchain4j.google-ai.model-name}")
//    private String modelName;

//    @Bean
//    public ChatLanguageModel chatLanguageModel() {
//        return GoogleAiGeminiChatModel.builder()
//                .apiKey(apiKey)
//                .modelName(modelName)
//                .temperature(0.1)
//                .build();
//    }
    @Bean
    public OpenAiChatModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("GROQ_API_KEY"))
                .baseUrl("https://api.groq.com/openai/v1")
                .modelName("llama-3.3-70b-versatile")
                .temperature(0.2)
                .build();
    }
}