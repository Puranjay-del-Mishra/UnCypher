package com.UnCypher.models.dto;

import lombok.Data;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class InsightResponse {

    private String userId;
    private String query;
    private List<String> intents;

    private String answer; // Chat answer → optional
    private String toolUsed;

    private Map<String, Object> meta;

    private MapCommandBatch mapCommandBatch; // Map commands → optional

    public static InsightResponse error(String message) {
        InsightResponse response = new InsightResponse();
        response.setAnswer(message);
        response.setToolUsed("none");
        response.setMeta(Collections.singletonMap("error", true));
        return response;
    }
}
