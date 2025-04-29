package com.UnCypher.models.dto;
import lombok.Data;
import java.util.Map;
import com.UnCypher.models.dto.SensorPayload;

@Data
public class InsightRequest {
    private String userId;
    private String query;
    private String location;
    private String timestamp; // ISO 8601

    // Optional: biometric or ambient data
    private SensorPayload sensors;
    private Map<String, String> context; // Arbitrary additional fields (weather, pageId, etc.)
}
