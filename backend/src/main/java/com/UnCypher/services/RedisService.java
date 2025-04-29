package com.UnCypher.services;

import com.UnCypher.models.UserStateCacheEntry;
import com.UnCypher.models.dto.PassiveInsightResponse;
import com.UnCypher.models.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, PassiveInsightResponse> passiveInsightRedis;
    private final RedisTemplate<String, UserStateCacheEntry> userStateCacheRedis;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, ChatMessage> chatRedisTemplate;

    // ===== Passive Insight Methods =====
    public void saveChatMessage(String userId, ChatMessage message) {
        String key = "chat:" + userId;
        chatRedisTemplate.opsForList().rightPush(key, message);
        chatRedisTemplate.expire(key, Duration.ofHours(6));
    }
    public List<ChatMessage> getChatHistory(String userId) {
        String key = "chat:" + userId;
        List<ChatMessage> chatMessages = chatRedisTemplate.opsForList().range(key, 0, -1);

        if (chatMessages == null) {
            System.out.println("⚠️ No chat history found for userId: " + userId);
            return new ArrayList<>();
        }

        System.out.println("✅ Fetched " + chatMessages.size() + " chats for userId: " + userId);
        return chatMessages;
    }

    public void cachePassiveInsight(String key, PassiveInsightResponse payload, Duration ttl) {
        passiveInsightRedis.opsForValue().set(key, payload, ttl);
    }

    public Optional<PassiveInsightResponse> getPassiveInsight(String key) {
        return Optional.ofNullable(passiveInsightRedis.opsForValue().get(key));
    }

    public void deletePassiveInsight(String key) {
        passiveInsightRedis.delete(key);
    }

    // ===== User State Cache Methods =====

    public void cacheUserState(String key, UserStateCacheEntry entry, Duration ttl) {
        userStateCacheRedis.opsForValue().set(key, entry, ttl);
    }

    public Optional<UserStateCacheEntry> getUserState(String key) {
        return Optional.ofNullable(userStateCacheRedis.opsForValue().get(key));
    }

    public void deleteUserState(String key) {
        userStateCacheRedis.delete(key);
    }

    // ===== Simple String Methods (for user:locality) =====

    public void cacheString(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    public Optional<String> getString(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    public void deleteString(String key) {
        stringRedisTemplate.delete(key);
    }
}

