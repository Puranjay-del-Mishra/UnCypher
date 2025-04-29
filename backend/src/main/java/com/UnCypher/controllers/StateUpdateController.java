package com.UnCypher.controllers;

import com.UnCypher.models.dto.UserStateRequest;
import com.UnCypher.services.UserStateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/state")
@RequiredArgsConstructor
public class StateUpdateController {

    private static final Logger log = LoggerFactory.getLogger(StateUpdateController.class);
    private final UserStateService userStateService;

    @PostMapping("/update")
    public ResponseEntity<Void> updateUserState(@RequestBody UserStateRequest request) {
        log.info("🔄 Processing state update for user {}", request.getUserId());
        userStateService.processUserState(request);
        return ResponseEntity.noContent().build(); // 204: no payload needed
    }
}
