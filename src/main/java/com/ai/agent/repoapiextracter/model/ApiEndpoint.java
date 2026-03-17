package com.ai.agent.repoapiextracter.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiEndpoint {

    private String method;
    private String path;
    private String sourceFile;
    private String requestSchema;
    private String responseSchema;

}