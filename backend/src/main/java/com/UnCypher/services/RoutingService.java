package com.UnCypher.services;

import com.UnCypher.models.dto.MapCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoutingService {

    @Value("${mapbox.api.key}")
    private String mapboxApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public MapCommand generateRouteCommand(List<Double> origin, List<Double> destination, String mode) {
        String profile = switch (mode.toLowerCase()) {
            case "walk", "walking" -> "walking";
            case "bike", "cycling" -> "cycling";
            default -> "driving";
        };

        String coordinates = String.format("%f,%f;%f,%f",
                origin.get(0), origin.get(1),
                destination.get(0), destination.get(1)
        );

        String url = String.format(
                "https://api.mapbox.com/directions/v5/mapbox/%s/%s?geometries=geojson&overview=full&alternatives=false&steps=true&access_token=%s",
                profile, coordinates, mapboxApiKey
        );

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("routes")) return null;

        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        if (routes.isEmpty()) return null;

        Map<String, Object> firstRoute = routes.get(0);
        Map<String, Object> geometry = (Map<String, Object>) firstRoute.get("geometry");

        double durationSeconds = ((Number) firstRoute.get("duration")).doubleValue();
        double distanceMeters = ((Number) firstRoute.get("distance")).doubleValue();

        String summary = String.format("Estimated time: %.1f min, Distance: %.1f km",
                durationSeconds / 60.0, distanceMeters / 1000.0);

        return MapCommand.builder()
                .type("add_route")
                .id("route-" + UUID.randomUUID())
                .color(getRouteColor(profile))
                .geojson(Map.of(
                        "type", "Feature",
                        "geometry", geometry,
                        "properties", Map.of(
                                "mode", profile,
                                "duration", durationSeconds,
                                "distance", distanceMeters,
                                "summary", summary
                        )
                ))
                .build();
    }

    private String getRouteColor(String mode) {
        return switch (mode) {
            case "walking" -> "green";
            case "cycling" -> "orange";
            default -> "blue";
        };
    }
}
