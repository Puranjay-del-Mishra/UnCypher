package com.UnCypher.controllers;

import com.UnCypher.models.dto.InsightRequest;
import com.UnCypher.models.dto.InsightResponse;
import com.UnCypher.models.dto.PassiveInsightRequest;
import com.UnCypher.models.dto.PassiveInsightResponse;
import com.UnCypher.models.dto.InsightAgentResponse;
import com.UnCypher.models.dto.MapCommandBatch;
import com.UnCypher.models.dto.ChatMessage;
import com.UnCypher.services.LLMService;
import com.UnCypher.services.RedisService;
import com.UnCypher.services.InsightResponseProcessor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightsController {

    private static final Logger logger = LoggerFactory.getLogger(InsightsController.class);
    private final LLMService llmService;
    private final RedisService redisService;
    private final InsightResponseProcessor insightResponseProcessor;

    @PostMapping("/test_ping")
    public ResponseEntity<String> testPing() {
        return ResponseEntity.ok("Insight service is reachable.");
    }

    @PostMapping("/passive")
    public ResponseEntity<PassiveInsightResponse> getPassiveInsights(
            @RequestBody PassiveInsightRequest request
    ) {
        logger.info("🔄 [UnCypher] Fetching passive insights for user: {}", request.getUserId());

        Map<String, Object> locationData = convertMapToObjectValues(request.getMeta());

        PassiveInsightResponse response = llmService.generatePassiveInsightFromAgent(
                request.getUserId(),
                "defaultLocality",
                locationData
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/query")
    public ResponseEntity<InsightResponse> getInsights(
            @RequestBody InsightRequest request
    ) {
        logger.info("💬 [UnCypher] Query insight for user: {}, query: {}", request.getUserId(), request.getQuery());

        // ✅ Extract context
        Map<String, String> context = request.getContext();

        // ✅ Build location string → Use lat/lng if available, otherwise fallback
        String locationString = "Unknown, Unknown";
        if (context != null && context.get("lat") != null && context.get("lng") != null) {
            locationString = context.get("lat") + "," + context.get("lng");
        }

        // ✅ Build AI request
        InsightRequest aiRequest = new InsightRequest();
        aiRequest.setUserId(request.getUserId() != null ? request.getUserId() : "anonymous");
        aiRequest.setQuery(request.getQuery());
        aiRequest.setLocation(locationString);
        aiRequest.setTimestamp(java.time.Instant.now().toString());
        aiRequest.setContext(context);

        // ✅ Call AI agent
        InsightAgentResponse agentResponse = llmService.callInsightAgent(aiRequest);

        InsightResponse response = new InsightResponse();
        response.setUserId(agentResponse.getUserId());
        response.setQuery(agentResponse.getQuery());
        response.setAnswer(agentResponse.getChatResponse());
        response.setIntents(agentResponse.getIntents());
        response.setMeta(Collections.singletonMap("source", "insight-agent"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat/save")
    public ResponseEntity<Void> saveChat(@RequestBody ChatMessage chatMessage) {
        redisService.saveChatMessage(chatMessage.getUserId(), chatMessage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String userId) {
        List<ChatMessage> chatHistory = redisService.getChatHistory(userId);
        return ResponseEntity.ok(chatHistory);
    }

    @PostMapping("/mapcommand")
    public ResponseEntity<Void> dispatchMapCommand(
            @RequestBody MapCommandBatch batch
    ) {
        logger.info("📍 [UnCypher] Dispatching MapCommands for user: {}", batch.getUserId());

        insightResponseProcessor.processMapCommands(batch.getUserId(), batch.getCommands());

        return ResponseEntity.ok().build();
    }

    // Helper: Convert Map<String, String> to Map<String, Object>
    private Map<String, Object> convertMapToObjectValues(Map<String, String> input) {
        if (input == null) {
            return Collections.emptyMap();
        }
        return input.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() != null ? e.getValue() : "null"
                ));
    }
}


