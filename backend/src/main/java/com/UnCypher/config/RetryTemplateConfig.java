package com.UnCypher.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class RetryTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(RetryTemplateConfig.class);

    @Bean
    public Retry aiAgentRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(IOException.class, RuntimeException.class)
                .build();

        Retry retry = Retry.of("ai-agent", config);

        retry.getEventPublisher().onRetry(event -> {
            logger.warn("Retrying AI agent call — attempt {} due to: {}",
                    event.getNumberOfRetryAttempts(),
                    event.getLastThrowable() != null ? event.getLastThrowable().toString() : "unknown error");
        });

        return retry;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // you can later wrap with builder + timeouts
    }
}

