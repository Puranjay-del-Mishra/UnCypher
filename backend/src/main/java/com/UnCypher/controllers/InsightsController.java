package com.UnCypher.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/insights")
public class InsightsController{
    @PostMapping("/test_ping")
    public ResponseEntity<Map<String, String>> testInsight(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        String headerToken = request.getHeader("X-XSRF-TOKEN");

        System.out.println("=== 🔍 CSRF Debug ===");
        System.out.println("🔐 Session ID: " + (session != null ? session.getId() : "null"));
        System.out.println("🍪 CSRF token in session (from attribute): " + (token != null ? token.getToken() : "null"));
        System.out.println("📨 CSRF token in header: " + headerToken);
        System.out.println("=====================");

        return ResponseEntity.ok(Map.of("message", "Insight received"));
    }
}