package com.UnCypher.controllers;

import com.UnCypher.models.dto.MapCommand;
import com.UnCypher.models.dto.POIInfoRequest;
import com.UnCypher.models.dto.POIInfoResponse;
import com.UnCypher.services.GeocodingService;
import com.UnCypher.services.GeocodingService.POI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/destination")
@RequiredArgsConstructor
public class DestinationController {

    private final GeocodingService geocodingService;

    @PostMapping("/resolve")
    public POIInfoResponse resolve(@RequestBody POIInfoRequest request) {
        POI poi = geocodingService.geocodeDestination(request.getCategory());

        POIInfoResponse response = new POIInfoResponse();
        if (poi != null) {
            MapCommand marker = new MapCommand();
            marker.setType("add_marker");
            marker.setId("dest-" + poi.getName().toLowerCase().replace(" ", "-"));
            marker.setCoords(List.of(poi.getLongitude(), poi.getLatitude())); // [lng, lat]
            marker.setPopupText("Destination: " + poi.getName());
            marker.setColor("yellow");
            response.setCommands(List.of(marker));
        } else {
            response.setCommands(Collections.emptyList());
        }

        return response;
    }
}
