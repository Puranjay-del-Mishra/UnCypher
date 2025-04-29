package com.UnCypher.config;

import com.UnCypher.models.UserStateCacheEntry;
import com.UnCypher.models.dto.PassiveInsightResponse;
import com.UnCypher.models.dto.ChatMessage;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class RedisConfig {
//    we can make a custom StringRedisTemplate and inject it using a custom bean name
//    @Bean
//    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory factory) {
//        RedisTemplate<String, String> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//        template.afterPropertiesSet();
//        return template;
//    }

    @Bean
    public RedisTemplate<String, PassiveInsightResponse> passiveInsightRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, PassiveInsightResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<PassiveInsightResponse> serializer =
                new Jackson2JsonRedisSerializer<>(PassiveInsightResponse.class);
        serializer.setObjectMapper(customObjectMapper());

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, UserStateCacheEntry> userStateCacheRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, UserStateCacheEntry> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<UserStateCacheEntry> serializer =
                new Jackson2JsonRedisSerializer<>(UserStateCacheEntry.class);
        serializer.setObjectMapper(customObjectMapper());

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
    @Bean
    public RedisTemplate<String, ChatMessage> chatRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<ChatMessage> serializer =
                new Jackson2JsonRedisSerializer<>(ChatMessage.class);
        serializer.setObjectMapper(customObjectMapper());

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }


    private ObjectMapper customObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        mapper.registerModule(new JavaTimeModule());  // 🛠️ FIX: Support Instant, LocalDateTime etc.
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: Write timestamps as ISO strings (human readable)

        return mapper;
    }
}



