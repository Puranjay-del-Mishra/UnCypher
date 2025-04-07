package com.UnCypher.models.dto;
import lombok.Data;
import java.util.Map;
@Data
public class InsightResponse {
    private String answer;
    private String toolUsed;
    private Map<String, Object> meta; // Contains e.g. source data, confidence
}
