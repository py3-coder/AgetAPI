package com.ai.agent.repoapiextracter.agent;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RepoTools {

    private Path rootPath;

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    //List ALL files recursively (VERY IMPORTANT for LLM)
    @Tool("Lists all files in the repository. Use this to explore available files. Paths are relative to repo root.")
    public String listFiles(String directory) {
        try {
            Path base = (directory == null || directory.isBlank())
                    ? rootPath
                    : rootPath.resolve(directory).normalize();

            // 🔒 Prevent traversal outside repo
            if (!base.startsWith(rootPath)) {
                return "ACCESS_DENIED";
            }

            if (!Files.exists(base)) {
                return "DIRECTORY_NOT_FOUND";
            }

            try (Stream<Path> paths = Files.walk(base)) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(p -> rootPath.relativize(p).toString().replace("\\", "/")) // ✅ normalize path
                        .collect(Collectors.joining("\n"));
            }

        } catch (Exception e) {
            return "ERROR_LISTING_FILES: " + e.getMessage();
        }
    }

    // Safe file reader (NO hallucination issues)
    @Tool("Reads a file from the repository. Provide exact relative path from listFiles output.")
    public String readFile(String relativePath) {
        try {
            if (relativePath == null || relativePath.isBlank()) {
                return "INVALID_PATH";
            }

            // Normalize path
            relativePath = relativePath.trim().replace("\\", "/");

            // Block traversal
            if (relativePath.contains("..")) {
                return "ACCESS_DENIED";
            }

            Path resolvedPath = rootPath.resolve(relativePath).normalize();

            // Ensure inside repo
            if (!resolvedPath.startsWith(rootPath)) {
                return "ACCESS_DENIED";
            }

            //Try direct file
            if (Files.exists(resolvedPath) && Files.isRegularFile(resolvedPath)) {
                return Files.readString(resolvedPath);
            }

            // Try adding common extensions (VERY IMPORTANT)
            String[] extensions = {".java", ".kt", ".go", ".js", ".ts", ".py"};

            for (String ext : extensions) {
                Path withExt = rootPath.resolve(relativePath + ext).normalize();

                if (withExt.startsWith(rootPath) &&
                        Files.exists(withExt) &&
                        Files.isRegularFile(withExt)) {

                    return Files.readString(withExt);
                }
            }
            return "FILE_NOT_FOUND: " + relativePath;

        } catch (IOException e) {
            return "ERROR_READING_FILE: " + e.getMessage();
        }
    }
}