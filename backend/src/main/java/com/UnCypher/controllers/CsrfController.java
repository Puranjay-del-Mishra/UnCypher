package com.UnCypher.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
@RestController
public class CsrfController {
    @GetMapping("/csrf-token")
    public ResponseEntity<Map<String, String>> csrf(HttpServletRequest request, CsrfToken token) {
        request.getSession(true); // force session
        System.out.println("🧪 CSRF token issued: " + token.getToken());
        System.out.println("🔐 Session ID: " + request.getSession().getId());
        return ResponseEntity.ok(Map.of("token", token.getToken()));
    }

}




