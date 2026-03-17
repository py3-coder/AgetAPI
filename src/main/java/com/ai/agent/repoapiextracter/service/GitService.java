package com.ai.agent.repoapiextracter.service;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitService {

    public Path cloneRepo(String url) throws Exception {

        Path tempDir = Files.createTempDirectory("repo_scan_");

        Git.cloneRepository()
                .setURI(url)
                .setDirectory(tempDir.toFile())
                .call();
        return tempDir;
    }
}