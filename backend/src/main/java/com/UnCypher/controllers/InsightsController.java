package com.UnCypher.controllers;

import com.UnCypher.models.dto.InsightRequest;
import com.UnCypher.models.dto.InsightResponse;
import com.UnCypher.models.dto.PassiveInsightRequest;
import com.UnCypher.models.dto.PassiveInsightResponse;
import com.UnCypher.models.dto.ChatMessage;
import com.UnCypher.services.LLMService;
import com.UnCypher.services.RedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightsController {

    private static final Logger logger = LoggerFactory.getLogger(InsightsController.class);
    private final LLMService llmService;
    private final RedisService redisService;

    @PostMapping("/test_ping")
    public ResponseEntity<String> testPing() {
        return ResponseEntity.ok("Insight service is reachable.");
    }

    @PostMapping("/passive")
    public ResponseEntity<PassiveInsightResponse> getPassiveInsights(
            @RequestBody PassiveInsightRequest request
    ) {
        logger.info("🔄 [UnCypher] Fetching passive insights for user: {}", request.getUserId());

        Map<String, Object> locationData = safeCast(request.getMeta());
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

        Map<String, Object> locationData = safeCast(request.getContext());
        InsightResponse response = llmService.generateInsightFromAgent(
                request.getUserId(),
                "defaultLocality",
                locationData,
                request.getQuery()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat/save")
    public ResponseEntity<Void> saveChat(@RequestBody ChatMessage chatMessage) {
        redisService.saveChatMessage(chatMessage.getUserId(), chatMessage);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String userId) {
        List<ChatMessage> chatHistory = redisService.getChatHistory(userId); // Assume it pulls from Redis list
        return ResponseEntity.ok(chatHistory);
    }


    @SuppressWarnings("unchecked")
    private Map<String, Object> safeCast(Map<String, String> input) {
        return input != null ? (Map<String, Object>) (Map<?, ?>) input : Collections.emptyMap();
    }

}

