package com.UnCypher.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/")
public class CsrfController {

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // ✅ Allow frontend origin
    @GetMapping("/csrf-token") // ✅ Defines the CSRF token retrieval endpoint
    public Map<String, String> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return Map.of("token", csrf.getToken());
    }
}
