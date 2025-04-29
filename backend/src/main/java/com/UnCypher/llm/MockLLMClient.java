package com.UnCypher.llm;

import org.springframework.stereotype.Component;

@Component
public class MockLLMClient implements LLMClient {

    @Override
    public String generate(String prompt) {
        // In production, replace with OpenAI/Ollama/HuggingFace API call
        return """
        Summary: You are near a highly active urban area.
        Tags: ["urban", "high-footfall", "daylight"]
        Recommendation: Consider nearby landmarks and safety tips.
        """;
    }
}
