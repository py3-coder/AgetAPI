package com.ai.agent.repoapiextracter.model;


import lombok.Data;
import java.util.Map;

@Data
public class ApiSchema {
    private String schemaName;       // e.g., "User"
    private String type;             // e.g., "object"
    private Map<String, String> fields; // Key: field name, Value: data type (e.g., "email": "string")
}