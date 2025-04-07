package com.UnCypher.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        System.out.println("🚫 Access denied on: " + request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String json = String.format(
                "{" +
                        "\"timestamp\": \"%s\"," +
                        "\"status\": 403," +
                        "\"error\": \"Forbidden\"," +
                        "\"message\": \"%s\"," +
                        "\"path\": \"%s\"" +
                        "}",
                Instant.now().toString(),
                accessDeniedException.getMessage(),
                request.getRequestURI()
        );

        response.getWriter().write(json);
    }
}
