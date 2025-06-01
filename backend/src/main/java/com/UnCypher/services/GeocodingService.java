package com.UnCypher.services;

import com.UnCypher.models.dto.MapCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    @Value("${mapbox.api.key}")
    private String mapboxApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public POI geocodeDestination(String placeName) {
        try {
            String encodedPlace = URLEncoder.encode(placeName, StandardCharsets.UTF_8);
            String url = String.format(
                    "https://api.mapbox.com/geocoding/v5/mapbox.places/%s.json?access_token=%s&limit=1",
                    encodedPlace, mapboxApiKey
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");

            if (features == null || features.isEmpty()) return null;

            Map<String, Object> feature = features.get(0);
            List<Double> coords = (List<Double>) feature.get("center");
            String name = (String) feature.get("place_name");

            return new POI("mapbox", name, coords.get(1), coords.get(0), 0.0);
        } catch (Exception e) {
            System.err.println("⚠️ GeocodingService failed to resolve: " + e.getMessage());
            return null;
        }
    }

    public static class POI {
        private final String id;
        private final String name;
        private final double latitude;
        private final double longitude;
        private final double distance;

        public POI(String id, String name, double latitude, double longitude, double distance) {
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distance = distance;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public double getDistance() { return distance; }
    }
}
