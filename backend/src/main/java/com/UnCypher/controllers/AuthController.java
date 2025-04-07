package com.UnCypher.controllers;

import com.UnCypher.models.AuthCred;
import com.UnCypher.security.JwtAuthenticationResponse;
import com.UnCypher.services.AuthService;
import com.UnCypher.models.dto.RefreshRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 🔐 Login endpoint: Accepts credentials and returns JWTs.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody AuthCred credentials) {
        JwtAuthenticationResponse tokens = authService.login(credentials.getEmail(), credentials.getPassword());
        return ResponseEntity.ok(tokens);
    }

    /**
     * ♻️ Refresh endpoint: Accepts refresh token and returns new access token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        JwtAuthenticationResponse tokens = authService.refreshToken(refreshRequest.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody AuthCred cred) {
        return authService.registerUser(cred);
    }

}


