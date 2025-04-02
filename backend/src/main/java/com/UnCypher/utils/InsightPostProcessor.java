package com.UnCypher.utils;

import com.UnCypher.models.dto.InsightResponseDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsightPostProcessor {

    public static InsightResponseDTO extractInsights(String rawOutput) {
        String[] lines = rawOutput.split("\n");

        String summary = null;
        List<String> tags = new ArrayList<>();
        String recommendation = null;

        for (String line : lines) {
            if (line.toLowerCase().startsWith("summary")) {
                summary = line.replaceFirst("(?i)summary\\s*[:：]", "").trim();
            } else if (line.toLowerCase().startsWith("tags")) {
                String tagLine = line.replaceFirst("(?i)tags\\s*[:：]", "").trim();
                tagLine = tagLine.replaceAll("[\\[\\]\"]", ""); // remove [ ] "
                tags = Arrays.asList(tagLine.split(",\\s*"));
            } else if (line.toLowerCase().startsWith("recommendation")) {
                recommendation = line.replaceFirst("(?i)recommendation\\s*[:：]", "").trim();
            }
        }

        return new InsightResponseDTO(summary, tags, recommendation);
    }
}
