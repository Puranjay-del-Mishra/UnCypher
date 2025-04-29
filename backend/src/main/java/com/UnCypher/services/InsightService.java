package com.UnCypher.services;

import com.UnCypher.models.dto.PassiveInsightResponse;
import com.UnCypher.models.dto.InsightResponse;

import java.util.Map;

public interface InsightService {
    PassiveInsightResponse getCachedInsight(String localityId);
    void saveInsightToCache(String localityId, PassiveInsightResponse response);
    InsightResponse generateInsightFromAgent(String userId, String localityId, Map<String, Object> locationData, String userQuery);
    PassiveInsightResponse generatePassiveInsightFromAgent(String userId, String localityId, Map<String, Object> locationData);
}
