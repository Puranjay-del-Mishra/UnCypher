package com.UnCypher.controllers;

import com.UnCypher.models.dto.RouteRequest;
import com.UnCypher.models.dto.MapCommand;
import com.UnCypher.services.RoutingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/navigation")
@RequiredArgsConstructor
public class NavigationController {

    private final RoutingService routingService;

    @PostMapping("/route")
    public MapCommand getRoute(@RequestBody RouteRequest request) {
        return routingService.generateRouteCommand(
                request.getOrigin(),
                request.getDestination(),
                request.getMode()
        );
    }
}
