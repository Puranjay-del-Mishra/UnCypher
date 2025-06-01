package com.UnCypher.controllers;

import com.UnCypher.models.dto.POIInfoRequest;
import com.UnCypher.models.dto.POIInfoResponse;
import com.UnCypher.models.dto.MapCommand;
import com.UnCypher.services.POIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/poi")
@RequiredArgsConstructor
public class POIController {

    private final POIService poiService;

    @PostMapping("/resolve-category")
    public POIInfoResponse resolveCategoryPOIs(@RequestBody POIInfoRequest request) {
        List<MapCommand> mapCommands;

        if (request.getCategory() == null || request.getCategory().isBlank()) {
            mapCommands = poiService.generateDefaultCategoryPOIs(
                    request.getUserId(),
                    request.getLocality()
            );
        } else {
            mapCommands = poiService.generateNearbyPOIMarkers(
                    request.getUserId(),
                    request.getCategory(),
                    request.getLocality()
            );
        }

        POIInfoResponse response = new POIInfoResponse();
        response.setCommands(mapCommands);
        return response;
    }

    @PostMapping("/resolve-snippets")
    public Map<String, Object> resolveCategorySnippets(@RequestBody POIInfoRequest request) {
        String category = request.getCategory() != null ? request.getCategory() : "interesting places";
        List<String> snippets = poiService.generatePOISnippets(request.getLocality(), category);
        return Map.of("snippets", snippets);
    }
}



