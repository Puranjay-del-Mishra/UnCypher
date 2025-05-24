package com.UnCypher.models.dto;

import lombok.Data;
import java.util.List;

@Data
public class InsightAgentResponse {

    private String userId;
    private String query;
    private String chatResponse;

    private List<String> intents; // POI, Navigation, Alert etc.
    private MapCommandBatch mapCommandBatch;

}
