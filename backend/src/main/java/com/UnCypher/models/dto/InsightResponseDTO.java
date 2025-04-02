package com.UnCypher.models.dto;

import java.util.List;

public class InsightResponseDTO {
    private String summary;
    private List<String> tags;
    private String recommendation;

    public InsightResponseDTO() {}

    public InsightResponseDTO(String summary, List<String> tags, String recommendation) {
        this.summary = summary;
        this.tags = tags;
        this.recommendation = recommendation;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}

