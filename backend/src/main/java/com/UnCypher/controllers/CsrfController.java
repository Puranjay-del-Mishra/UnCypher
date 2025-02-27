package com.UnCypher.controllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(CsrfController.class);
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // ✅ Allow frontend origin
    @GetMapping("/csrf-token") // ✅ Defines the CSRF token retrieval endpoint
    public Map<String, String> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            logger.info("🔐 Issued CSRF Token: " + csrf.getToken());
            return Map.of("token", csrf.getToken());
        } else {
            logger.warn("⚠️ CSRF Token not found in request attributes.");
            return Map.of("error", "CSRF Token not available.");
        }
    }
}
