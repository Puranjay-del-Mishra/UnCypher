package com.UnCypher.models.dto;
import lombok.Data;
import java.util.Map;

@Data
public class PassiveInsightRequest {
    private String userId;
    private String location;
    private String timestamp; // ISO 8601

    // Optional context
    private String deviceType;         // "mobile", "wearable", "vehicle", etc.
    private Map<String, String> meta;  // extensible context (e.g. mood, timezone, appVersion)
}
