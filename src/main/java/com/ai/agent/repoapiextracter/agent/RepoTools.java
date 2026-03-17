package com.ai.agent.repoapiextracter.agent;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class RepoTools {

    private Path rootPath;

    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Tool("Lists files in a directory to understand the project structure")
    public String listFiles(String directory) throws Exception {

        Path targetDir = rootPath.resolve(directory);

        return Files.list(targetDir)
                .map(p -> p.getFileName().toString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Empty directory");
    }

    @Tool("Reads the content of a file to extract API and Schema details")
    public String readFile(String relativePath) throws Exception {
        return Files.readString(rootPath.resolve(relativePath));
    }
}