package com.UnCypher.config;

import com.UnCypher.security.JwtAuthenticationFilter;
import com.UnCypher.security.JwtAuthEntryPoint;
import com.UnCypher.security.JwtAccessDeniedHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfFilter;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final List<String> allowedOrigins;

    public SecurityConfig(@Value("#{'${cors.allowed.origins}'.split(',')}") List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
        logger.info("✅ SecurityConfig is being initialized with origins: {}", allowedOrigins);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthFilter,
                                                   JwtAuthEntryPoint unauthorizedHandler,
                                                   JwtAccessDeniedHandler accessDeniedHandler) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // ✅ Disable CSRF for JWT stateless API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/auth/refresh").permitAll()
                        .requestMatchers("/api/poi/resolve-destination").permitAll()
                        .requestMatchers("/api/navigation/route").permitAll()
                        .requestMatchers("/api/insights/mapcommand").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/generate-agent-token").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/insights/**").hasAnyRole("USER", "ANALYST", "ADMIN", "AGENT")
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(csrfTokenBindingFilter(), CsrfFilter.class)
                .addFilterAfter(authDebugLogger(), JwtAuthenticationFilter.class)
                .addFilterAfter(new CsrfDebugFilter(), CsrfFilter.class);

        return http.build();
    }

    // Optional: if supporting frontend that reads token from cookies
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repo.setHeaderName("X-XSRF-TOKEN");
        repo.setCookieName("XSRF-TOKEN");
        repo.setCookiePath("/");
        return repo;
    }

    // Binds CSRF token to Spring's expected request attribute (only needed for hybrid scenarios)
    @Bean
    public OncePerRequestFilter csrfTokenBindingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {

                CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
                if (token != null) {
                    request.setAttribute(CsrfToken.class.getName(), token);
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    // Logs current auth state on every request (debug only, remove in prod)
    @Bean
    public OncePerRequestFilter authDebugLogger() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {
                var auth = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("=== 🔐 AUTH DEBUG ===");
                System.out.println("🔑 Principal: " + (auth != null ? auth.getPrincipal() : "null"));
                System.out.println("✅ Authenticated: " + (auth != null && auth.isAuthenticated()));
                System.out.println("📍 Endpoint: " + request.getRequestURI());
                System.out.println("======================");
                filterChain.doFilter(request, response);
            }
        };
    }

    // Global CORS configuration for mobile + web
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(allowedOrigins); // e.g. [http://localhost:3000]
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        config.setExposedHeaders(List.of("Authorization", "X-CSRF-TOKEN"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Register explicitly for SockJS info route
        source.registerCorsConfiguration("/ws/**", config);
        source.registerCorsConfiguration("/ws/info", config); // 👈 critical for SockJS preflight
        source.registerCorsConfiguration("/**", config); // fallback for general API

        return source;
    }

    // 🧪 CSRF Debug Logger — visible in console logs
    public static class CsrfDebugFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {

            HttpSession session = request.getSession(false);
            CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            String headerToken = request.getHeader("X-XSRF-TOKEN");
            String cookieToken = null;

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("XSRF-TOKEN".equals(cookie.getName())) {
                        cookieToken = cookie.getValue();
                    }
                }
            }

            System.out.println("=== 🧪 CSRF Debug Filter ===");
            System.out.println("🔐 Session ID: " + (session != null ? session.getId() : "null"));
            System.out.println("🍪 Cookie XSRF-TOKEN: " + cookieToken);
            System.out.println("📨 Header X-XSRF-TOKEN: " + headerToken);
            System.out.println("🔖 CSRF token from request attribute: " + (token != null ? token.getToken() : "null"));
            System.out.println("🎯 Request URI: " + request.getRequestURI());
            System.out.println("=================================");

            filterChain.doFilter(request, response);
        }
    }
}
