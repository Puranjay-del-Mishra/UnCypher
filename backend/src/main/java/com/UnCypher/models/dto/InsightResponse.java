package com.UnCypher.models.dto;

import lombok.Data;
import java.util.Collections;
import java.util.Map;

@Data
public class InsightResponse {
    private String answer;
    private String toolUsed;
    private Map<String, Object> meta;

    public static InsightResponse error(String message) {
        InsightResponse response = new InsightResponse();
        response.setAnswer(message);
        response.setToolUsed("none");
        response.setMeta(Collections.singletonMap("error", true));
        return response;
    }
}

