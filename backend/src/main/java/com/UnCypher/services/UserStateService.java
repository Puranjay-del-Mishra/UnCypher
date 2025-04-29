package com.UnCypher.services;

import com.UnCypher.models.UserStateCacheEntry;
import com.UnCypher.models.dto.PassiveInsightResponse;
import com.UnCypher.models.dto.UserStateRequest;
import com.UnCypher.utils.StateComparator;
import com.UnCypher.utils.UserStateSchemaProvider;
import com.UnCypher.mappers.InsightSocketPayloadMapper;
import lombok.RequiredArgsConstructor;
import com.UnCypher.services.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStateService {

    private static final Logger log = LoggerFactory.getLogger(UserStateService.class);

    private final RedisService redisService;
    private final LocalityResolver localityResolver;
    private final LLMService llmService;
    private final SimpMessagingTemplate messagingTemplate;

    public void processUserState(UserStateRequest request) {
        final String userId = request.getUserId();
        final Map<String, Object> currentState = request.getState();
        final String deviceType = (String) currentState.get("device");

        final Map<String, Object> schema = UserStateSchemaProvider.getSchemaForUser(userId, deviceType);
        final UserStateCacheEntry cachedEntry = redisService.getUserState("user:state:" + userId).orElse(null);
        final Map<String, Object> previousState = cachedEntry != null ? cachedEntry.getState() : null;

        UserStateCacheEntry updatedEntry = new UserStateCacheEntry();
        updatedEntry.setState(currentState);
        updatedEntry.setLastUpdated(Instant.now().toString());

        updateLocalityAndInsights(userId, currentState, previousState, schema, updatedEntry);
    }

    @SuppressWarnings("unchecked")
    private void updateLocalityAndInsights(
            String userId,
            Map<String, Object> currentState,
            Map<String, Object> previousState,
            Map<String, Object> schema,
            UserStateCacheEntry updatedEntry
    ) {
        Map<String, Object> location = (Map<String, Object>) currentState.get("location");
        if (location == null || location.get("lat") == null || location.get("lng") == null) return;

        double lat = Double.parseDouble(location.get("lat").toString());
        double lng = Double.parseDouble(location.get("lng").toString());

        String newLocalityId = localityResolver.resolveLocalityId(lat, lng);
        updatedEntry.setLocalityId(newLocalityId);

        String localityCacheKey = "user:locality:" + userId;
        String cachedLocalityId = redisService.getString(localityCacheKey).orElse(null);

        boolean localityChanged = StateComparator.hasStateChanged(currentState, previousState, schema);

        log.debug("\uD83D\uDCCD [{}] Previous locality: {}, New: {}, Changed? {}", userId, cachedLocalityId, newLocalityId, localityChanged);

        PassiveInsightResponse insight = redisService.getPassiveInsight(newLocalityId)
                .orElseGet(() -> {
                    PassiveInsightResponse generated = llmService.generatePassiveInsightFromAgent(userId, newLocalityId, location);
                    redisService.cachePassiveInsight(newLocalityId, generated, Duration.ofHours(6));
                    return generated;
                });

        Map<String, Object> socketPayload = InsightSocketPayloadMapper.toSocketPayload(
                userId,
                newLocalityId,
                insight,
                "location",
                location.getOrDefault("city", "Unknown") + ", " + location.getOrDefault("country", "Unknown")
        );

        if (localityChanged || cachedLocalityId == null) {
            pushPassiveInsightToUser(socketPayload, userId);
            redisService.cacheString("user:locality:" + userId, newLocalityId, Duration.ofHours(6));
        }

        redisService.cacheUserState("user:state:" + userId, updatedEntry, Duration.ofHours(6));
    }

    public void pushPassiveInsightToUser(Map<String, Object> socketPayload, String userId) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/passiveInsight", socketPayload);
    }
}





