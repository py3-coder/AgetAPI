package com.ai.agent.repoapiextracter.model;

import lombok.Data;
import java.util.List;

@Data
public class RepositoryStructure {
    private List<ApiEndpoint> endpoints;
}

