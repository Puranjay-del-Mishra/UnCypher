package com.UnCypher.services;

import com.UnCypher.models.dto.MapCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class POIService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${poi.provider.foursquare.apiKey}")
    private String foursquareApiKey;

    public MapCommand getDestinationMarker(String userId, String destination, String locality) {
        POI poi = getBestPOIMatch(locality, destination);
        if (poi == null) return null;

        MapCommand marker = new MapCommand();
        marker.setType("add_marker");
        marker.setId("dest-" + destination.toLowerCase().replace(" ", "-"));
        marker.setCoords(List.of(poi.getLongitude(), poi.getLatitude()));
        marker.setPopupText("Destination: " + poi.getName());
        marker.setColor("yellow");
        return marker;
    }

    public POI getBestPOIMatch(String locality, String destination) {
        List<POI> pois = searchPOIs(locality, destination);
        if (pois.isEmpty()) return null;

        POI bestMatch = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (POI poi : pois) {
            double score = 0;
            if (poi.getName().equalsIgnoreCase(destination)) {
                score += 100;
            } else if (poi.getName().toLowerCase().contains(destination.toLowerCase())) {
                score += 50;
            }

            if (poi.getDistance() > 0) {
                score += 1000.0 / (1.0 + poi.getDistance());
            }

            if (score > bestScore) {
                bestScore = score;
                bestMatch = poi;
            }
        }

        return bestMatch;
    }

    public List<POI> searchPOIs(String locality, String category) {
        String url = "https://api.foursquare.com/v3/places/search?query=" + category +
                "&near=" + locality + "&limit=10";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", foursquareApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Map response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        List<POI> pois = new ArrayList<>();

        if (results != null) {
            for (Map<String, Object> result : results) {
                String id = (String) result.get("fsq_id");
                String name = (String) result.get("name");

                Map<String, Object> geocodes = (Map<String, Object>) result.get("geocodes");
                Map<String, Object> main = (Map<String, Object>) geocodes.get("main");
                double latitude = (double) main.get("latitude");
                double longitude = (double) main.get("longitude");

                double distance = result.get("distance") instanceof Number ?
                        ((Number) result.get("distance")).doubleValue() : 0.0;

                pois.add(new POI(id, name, latitude, longitude, distance));
            }
        }

        return pois;
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
