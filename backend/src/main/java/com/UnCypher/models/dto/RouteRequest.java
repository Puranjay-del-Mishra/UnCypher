package com.UnCypher.models.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouteRequest {
    private List<Double> origin;      // [longitude, latitude]
    private List<Double> destination; // [longitude, latitude]
    private String mode;              // "driving", "walking", or "cycling"
}
