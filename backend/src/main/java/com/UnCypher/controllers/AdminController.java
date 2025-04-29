package com.UnCypher.controllers;

import com.UnCypher.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JwtUtil jwtUtil;

    @GetMapping("/generate-agent-token")
    public ResponseEntity<Map<String, String>> generateAgentToken() {
        // Dummy UserDetails with AGENT role
        UserDetails user = User.withUsername("insight-agent")
                .password("dummy")  // Not used
                .roles("AGENT")
                .build();

        String token = jwtUtil.generateAccessToken("insight-agent", user);
        return ResponseEntity.ok(Collections.singletonMap("accessToken", token));
    }
}
