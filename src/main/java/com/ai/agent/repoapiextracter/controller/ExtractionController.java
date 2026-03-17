package com.ai.agent.repoapiextracter.controller;

import com.ai.agent.repoapiextracter.model.RepositoryStructure;
import com.ai.agent.repoapiextracter.service.ExtractionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExtractionController {

    private final ExtractionService service;

    public ExtractionController(ExtractionService service) {
        this.service = service;
    }

    @GetMapping("/extract")
    public RepositoryStructure extract(@RequestParam String repoUrl) throws Exception {
        return service.extract(repoUrl);
    }
}