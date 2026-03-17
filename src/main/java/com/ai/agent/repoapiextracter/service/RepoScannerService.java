package com.ai.agent.repoapiextracter.service;

import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.util.*;

@Service
public class RepoScannerService {

    public List<Path> findApiFiles(Path root) throws Exception {

        List<Path> result = new ArrayList<>();

        Files.walk(root)
                .filter(p -> p.toString().endsWith(".js")
                        || p.toString().endsWith(".ts")
                        || p.toString().endsWith(".java"))
                .forEach(result::add);

        return result;
    }
}