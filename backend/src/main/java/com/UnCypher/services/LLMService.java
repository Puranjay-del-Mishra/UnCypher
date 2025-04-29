package com.UnCypher.services;

import com.UnCypher.models.dto.*;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LLMService implements InsightService {

    private final RestTemplate restTemplate;
    private final Retry aiAgentRetry;
    private final RedisTemplate<String, PassiveInsightResponse> redisTemplate;

    @Value("${insight.agent.url}")
    private String insightAgentUrl;

    public PassiveInsightResponse getPassiveInsights(PassiveInsightRequest request) {
        return Try.ofSupplier(
                Retry.decorateSupplier(aiAgentRetry, () ->
                        restTemplate.postForObject(
                                insightAgentUrl + "/infer-passive",
                                request,
                                PassiveInsightResponse.class
                        )
                )
        ).get();
    }

    public InsightResponse getInsights(InsightRequest request) {
        return Try.ofSupplier(
                Retry.decorateSupplier(aiAgentRetry, () ->
                        restTemplate.postForObject(
                                insightAgentUrl + "/infer",
                                request,
                                InsightResponse.class
                        )
                )
        ).get();
    }

    @Override
    public PassiveInsightResponse getCachedInsight(String localityId) {
        return redisTemplate.opsForValue().get("locality:" + localityId);
    }

    @Override
    public void saveInsightToCache(String localityId, PassiveInsightResponse response) {
        redisTemplate.opsForValue().set("locality:" + localityId, response, Duration.ofHours(6));
    }

    @Override
    public InsightResponse generateInsightFromAgent(String userId, String localityId, Map<String, Object> locationData, String userQuery) {
        InsightRequest request = new InsightRequest();
        request.setUserId(userId != null ? userId : "anonymous");
        request.setQuery(userQuery);
        request.setLocation(buildLocationString(locationData));
        request.setTimestamp(java.time.Instant.now().toString());

        // Full context
        Map<String, String> context = convertMapToStringValues(locationData);
        request.setContext(context);

        // Optional: if you have specific sensor mapping logic, plug here
        // request.setSensors(sensorPayloadMapper.map(locationData));

        return getInsights(request);
    }

    @Override
    public PassiveInsightResponse generatePassiveInsightFromAgent(String userId, String localityId, Map<String, Object> locationData) {
        PassiveInsightRequest request = new PassiveInsightRequest();
        request.setUserId(userId != null ? userId : "anonymous");
        request.setLocation(buildLocationString(locationData));
        request.setTimestamp(java.time.Instant.now().toString());
        request.setDeviceType((String) locationData.getOrDefault("device", "unknown"));

        Map<String, String> meta = convertMapToStringValues(locationData);
        request.setMeta(meta);

        return getPassiveInsights(request);
    }

    // Helpers
    private String buildLocationString(Map<String, Object> locationData) {
        return locationData.getOrDefault("city", "Unknown") + ", " +
                locationData.getOrDefault("country", "Unknown");
    }

    private Map<String, String> convertMapToStringValues(Map<String, Object> input) {
        if (input == null) {
            return Collections.emptyMap();
        }
        return input.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() != null ? e.getValue().toString() : "null"
                ));
    }
}
