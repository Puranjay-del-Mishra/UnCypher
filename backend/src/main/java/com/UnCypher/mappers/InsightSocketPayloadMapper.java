package com.UnCypher.mappers;

import com.UnCypher.models.dto.PassiveInsightResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class InsightSocketPayloadMapper {

    public static Map<String, Object> toSocketPayload(
            String userId,
            String localityId,
            PassiveInsightResponse response,
            String triggerType,
            String rawLocation) {

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "PASSIVE_INSIGHT_UPDATE");

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("localityId", localityId);
        body.put("location", rawLocation);
        body.put("trigger", triggerType);
        body.put("timestamp", Instant.now().toString());
        body.put("insights", response.getInsights());
        body.put("toolUsed", response.getToolUsed());
        body.put("meta", response.getMeta());

        envelope.put("payload", body);
        return envelope;
    }
}
