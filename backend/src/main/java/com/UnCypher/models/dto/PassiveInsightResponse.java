package com.UnCypher.models.dto;
import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class PassiveInsightResponse {
    private List<String> insights;    // List of generated insight statements
    private String toolUsed;          // e.g., "insight-feed", "activity-summarizer"
    private Map<String, Object> meta; // Optional: models used, timestamps, raw stats
}
