package com.ai.agent.repoapiextracter.service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.io.File;
import java.util.*;

public class SchemaExtractor {

    public Map<String, Object> extractSchema(File file) throws Exception {

        CompilationUnit cu = StaticJavaParser.parse(file);

        Map<String, Object> schema = new LinkedHashMap<>();
        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        for (FieldDeclaration field : cu.findAll(FieldDeclaration.class)) {

            String name = field.getVariable(0).getNameAsString();
            String type = field.getElementType().asString();

            Map<String, String> fieldSchema = new HashMap<>();
            fieldSchema.put("type", mapType(type));

            properties.put(name, fieldSchema);

            required.add(name);
        }

        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", required);

        return schema;
    }

    private String mapType(String type) {
        return switch (type) {
            case "int", "Integer", "long", "Long" -> "integer";
            case "String" -> "string";
            case "boolean", "Boolean" -> "boolean";
            default -> "string";
        };
    }
}