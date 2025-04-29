package com.UnCypher.models.dto;

import lombok.Data;
import java.util.Map;

@Data
public class UserStateRequest {
    private String userId;
    private String timestamp;
    private Map<String, Object> state; // Nested structure from frontend
}
