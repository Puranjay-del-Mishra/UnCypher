package com.UnCypher.services;

import com.UnCypher.models.dto.MapCommand;
import com.UnCypher.models.dto.POISnippets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class POIService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${poi.provider.foursquare.apiKey}")
    private String foursquareApiKey;

    public List<MapCommand> generateNearbyPOIMarkers(String userId, String category, String locality) {
        List<POI> pois = searchMinimalPOIs(locality, category);
        List<MapCommand> mapCommands = new ArrayList<>();

        for (POI poi : pois) {
            MapCommand marker = new MapCommand();
            marker.setType("add_marker");
            marker.setId("poi-" + poi.getId());
            marker.setCoords(List.of(poi.getLongitude(), poi.getLatitude()));
            marker.setPopupText(poi.getName());
            marker.setColor("blue");
            mapCommands.add(marker);
        }

        return mapCommands;
    }

    public List<MapCommand> generateDefaultCategoryPOIs(String userId, String locality) {
        List<String> defaultCategories = List.of("landmark", "park", "cafe", "museum", "attraction");
        List<MapCommand> allMarkers = new ArrayList<>();

        for (String category : defaultCategories) {
            List<POI> pois = searchMinimalPOIs(locality, category);
            for (POI poi : pois) {
                MapCommand marker = new MapCommand();
                marker.setType("add_marker");
                marker.setId("poi-" + poi.getId() + "-" + category);
                marker.setCoords(List.of(poi.getLongitude(), poi.getLatitude()));
                marker.setPopupText(poi.getName() + " (" + category + ")");
                marker.setColor("blue");
                allMarkers.add(marker);
            }
        }

        return allMarkers;
    }

    public List<String> generatePOISnippets(String locality, String category) {
        List<POISnippets> pois = searchPOISnippets(locality, category);
        List<String> snippets = new ArrayList<>();

        for (POISnippets poi : pois) {
            String snippet = String.format(
                    "%s (%s) — %s, %s [%s]. %s %d meters away. %s",
                    poi.getName(),
                    poi.getPrimaryCategory(),
                    poi.getFormattedAddress(),
                    poi.getLocality(),
                    poi.getRegion(),
                    poi.getCrossStreet() != null ? "Near " + poi.getCrossStreet() + "." : "",
                    poi.getDistance(),
                    poi.getChainName() != null ? "Part of " + poi.getChainName() + "." : ""
            );
            snippets.add(snippet);
        }

        return snippets;
    }

    // Uses minimal POI model — for marker commands
    private List<POI> searchMinimalPOIs(String locality, String category) {
        List<POISnippets> fullPOIs = searchPOISnippets(locality, category);
        List<POI> minimal = new ArrayList<>();

        for (POISnippets p : fullPOIs) {
            minimal.add(new POI(p.getId(), p.getName(), p.getLatitude(), p.getLongitude()));
        }

        return minimal;
    }

    // Uses rich snippet DTO
    public List<POISnippets> searchPOISnippets(String locality, String category) {
        String param = locality.matches("^-?\\d{1,3}\\.\\d+,-?\\d{1,3}\\.\\d+$") ? "ll=" : "near=";
        String url = "https://api.foursquare.com/v3/places/search?query=" + category +
                "&" + param + locality + "&limit=10";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", foursquareApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            Map response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            System.out.println("🌐 Requesting Foursquare: " + url);
            System.out.println("📦 Raw Foursquare response: " + response);
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            List<POISnippets> pois = new ArrayList<>();
            if (results != null) {
                for (Map<String, Object> result : results) {
                    String id = (String) result.get("fsq_id");
                    String name = (String) result.get("name");

                    Map<String, Object> geocodes = (Map<String, Object>) result.get("geocodes");
                    Map<String, Object> main = (Map<String, Object>) geocodes.get("main");
                    double latitude = (double) main.get("latitude");
                    double longitude = (double) main.get("longitude");

                    Map<String, Object> location = (Map<String, Object>) result.get("location");
                    String formattedAddress = (String) location.getOrDefault("formatted_address", "");
                    String localityName = (String) location.getOrDefault("locality", "");
                    String region = (String) location.getOrDefault("region", "");
                    String country = (String) location.getOrDefault("country", "");
                    String crossStreet = (String) location.getOrDefault("cross_street", "");
                    String postcode = (String) location.getOrDefault("postcode", "");

                    int distance = (int) result.getOrDefault("distance", 0);
                    String timezone = (String) result.getOrDefault("timezone", "");
                    String status = (String) result.getOrDefault("closed_bucket", "open");

                    List<Map<String, Object>> categories = (List<Map<String, Object>>) result.get("categories");
                    String primaryCategory = categories.isEmpty() ? "Place" : (String) categories.get(0).get("name");

                    String categoryIcon = "";
                    if (!categories.isEmpty()) {
                        Map<String, Object> icon = (Map<String, Object>) categories.get(0).get("icon");
                        categoryIcon = icon.get("prefix") + "64" + icon.get("suffix");
                    }

                    List<Map<String, Object>> chains = (List<Map<String, Object>>) result.getOrDefault("chains", new ArrayList<>());
                    String chainName = chains.isEmpty() ? null : (String) chains.get(0).get("name");

                    pois.add(new POISnippets(id, name, latitude, longitude, distance, formattedAddress, localityName,
                            region, country, crossStreet, postcode, timezone, primaryCategory,
                            categoryIcon, chainName, status));
                }
            }

            return pois;
        } catch (Exception e) {
            System.err.println("⚠️ Failed to fetch POIs from Foursquare: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Embedded legacy POI DTO for marker logic
    public static class POI {
        private final String id;
        private final String name;
        private final double latitude;
        private final double longitude;

        public POI(String id, String name, double latitude, double longitude) {
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }
}




