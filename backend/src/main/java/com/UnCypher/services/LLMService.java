package com.UnCypher.services;

import com.UnCypher.utils.InsightPreProcessor;
import com.UnCypher.llm.LLMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LLMService{

    private final LLMClient llmClient;

    @Autowired
    public LLMService(LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    public Map<String, Object> generateInsights(Map<String, Object> rawInput) {
        // Step 1: Preprocess
        String prompt = InsightPreProcessor.preprocessToPrompt(rawInput);
        System.out.println("🧠 LLM Prompt:\n" + prompt);

        // Step 2: Generate using LLM
        String rawLLMOutput = llmClient.generate(prompt);

        // Step 3: Package for post-processing
        Map<String, Object> result = new HashMap<>();
        result.put("prompt", prompt);
        result.put("llmRawOutput", rawLLMOutput);
        return result;
    }
}

