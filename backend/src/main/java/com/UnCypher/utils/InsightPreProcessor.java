package com.UnCypher.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class InsightPreProcessor {

    private static final Pattern MULTISPACE = Pattern.compile("\\s+");

    public static String preprocessToPrompt(Map<String, Object> input) {
        StringBuilder prompt = new StringBuilder();

        // 🧭 Start with location context
        if (input.containsKey("city") || input.containsKey("country")) {
            prompt.append("User is located at: ")
                    .append(input.getOrDefault("city", "Unknown City")).append(", ")
                    .append(input.getOrDefault("country", "Unknown Country")).append(".\n");
        }

        // 🛰️ Add lat/lng and accuracy
        prompt.append("Coordinates: (")
                .append(input.getOrDefault("lat", "N/A")).append(", ")
                .append(input.getOrDefault("lng", "N/A")).append("), ")
                .append("Accuracy: ±").append(input.getOrDefault("accuracy", "N/A")).append(" meters.\n");

        // 🧠 Optional user input
        if (input.containsKey("userInput")) {
            String userInput = ((String) input.get("userInput")).trim();
            userInput = MULTISPACE.matcher(userInput).replaceAll(" ");
            prompt.append("User says: \"").append(userInput).append("\"\n");
        }

        // 🧪 Unique pre-processing: inject inferred metadata
        if (input.containsKey("city") && "New York".equalsIgnoreCase((String) input.get("city"))) {
            prompt.append("Note: NYC is dense and noisy. Consider environment-aware insights.\n");
        }

        return prompt.toString();
    }
}
