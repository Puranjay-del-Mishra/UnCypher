package com.UnCypher.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        // Log for visibility (optional)
        System.out.println("⛔ Unauthorized access attempt to: " + request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Optional: include more info in error body
        String json = String.format(
                "{" +
                        "\"timestamp\": \"%s\"," +
                        "\"status\": 401," +
                        "\"error\": \"Unauthorized\"," +
                        "\"message\": \"%s\"," +
                        "\"path\": \"%s\"" +
                        "}",
                Instant.now().toString(),
                authException.getMessage(),
                request.getRequestURI()
        );

        response.getWriter().write(json);
    }
}
