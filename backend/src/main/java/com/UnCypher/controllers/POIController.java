package com.UnCypher.controllers;

import com.UnCypher.models.dto.POIInfoRequest;
import com.UnCypher.models.dto.POIInfoResponse;
import com.UnCypher.models.dto.MapCommand;
import com.UnCypher.services.POIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/poi")
@RequiredArgsConstructor
public class POIController {

    private final POIService poiService;

//    @PostMapping("/info")
//    public POIInfoResponse getPOIInfo(@RequestBody POIInfoRequest request) {
//
//        // Generate map markers for requested category + locality
//        List<MapCommand> mapCommands = poiService.generatePOIMarkers(
//                request.getUserId(),
//                request.getCategory(),
//                request.getLocality()
//        );
//
//        // Prepare response
//        POIInfoResponse response = new POIInfoResponse();
//        response.setCommands(mapCommands);
//
//        return response;
//    }

    @PostMapping("/resolve-destination")
    public POIInfoResponse resolveDestinationMarker(@RequestBody POIInfoRequest request){
        MapCommand bestMatch = poiService.getDestinationMarker(
                request.getUserId(),
                request.getCategory(),
                request.getLocality()
        );

        POIInfoResponse response = new POIInfoResponse();
        if (bestMatch != null) {
            response.setCommands(List.of(bestMatch));
        } else {
            response.setCommands(Collections.emptyList());
        }

        return response;
    }

}

