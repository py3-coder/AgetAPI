package com.ai.agent.repoapiextracter.service;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CodeSnippetExtractorService {

    public String extractSnippet(Path file, String endpointPath) {

        try {
            String content = Files.readString(file);

            int index = content.indexOf(endpointPath);

            if (index == -1) return content.substring(0, Math.min(2000, content.length()));

            // Take window around endpoint
            int start = Math.max(0, index - 500);
            int end = Math.min(content.length(), index + 1000);

            return content.substring(start, end);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}