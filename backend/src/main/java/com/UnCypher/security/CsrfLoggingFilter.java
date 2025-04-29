package com.UnCypher.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;

public class CsrfLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(CsrfLoggingFilter.class);
    private final CsrfTokenRepository csrfTokenRepository;

    public CsrfLoggingFilter(CsrfTokenRepository csrfTokenRepository) {
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get CSRF Token from the request attributes
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        // Get CSRF Token from headers
        String headerToken = request.getHeader("X-XSRF-TOKEN");

        // Get CSRF Token from cookies
        String cookieToken = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("XSRF-TOKEN"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("NOT FOUND");

        // Log all CSRF sources
//        logger.info("🔹 [Backend] Expected CSRF Token (Spring): {}", csrfToken != null ? csrfToken.getToken() : "NOT FOUND");
//        logger.info("🔹 [Request Header] X-XSRF-TOKEN: {}", headerToken != null ? headerToken : "NOT FOUND");
//        logger.info("🔹 [Request Cookie] XSRF-TOKEN: {}", cookieToken);

        filterChain.doFilter(request, response);
    }
}


