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
import com.UnCypher.services.LLMService;
import com.UnCypher.utils.InsightPostProcessor;
import com.UnCypher.utils.InsightPreProcessor;
import org.springframework.web.bind.annotation.RequestBody;
import com.UnCypher.models.dto.InsightResponseDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/insights")
public class InsightsController{
    private final LLMService llmService;

    public InsightsController(LLMService llmService) {
        this.llmService = llmService;
    }

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

    @PostMapping("/basic_insights")
    public ResponseEntity<InsightResponseDTO> getBasicInsights(HttpServletRequest request, @RequestBody Map<String, Object> requestData) {
        HttpSession session = request.getSession(true);
//        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        String headerToken = request.getHeader("X-XSRF-TOKEN");
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔍 Auth in controller: " + (auth != null ? auth.getName() : "null"));
        System.out.println("🔐 Is authenticated: " + (auth != null && auth.isAuthenticated()));


        System.out.println("⚙️  Token at controller: " + (token != null ? token.getToken() : "null"));

        System.out.println("🔐 Session ID: " + (session != null ? session.getId() : "null"));
        System.out.println("🍪 XSRF-TOKEN (cookie): " + headerToken);
        System.out.println("🔖 CSRF token from attribute: " + (token != null ? token.getToken() : "null"));

        Map<String, Object> llmResult = llmService.generateInsights(requestData);
        String rawLLMOutput = (String) llmResult.get("llmRawOutput");

        InsightResponseDTO structured = InsightPostProcessor.extractInsights(rawLLMOutput);
        return ResponseEntity.ok(structured);
    }
}