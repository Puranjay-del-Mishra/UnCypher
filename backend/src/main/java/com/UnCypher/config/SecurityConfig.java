package com.UnCypher.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
        logger.info("✅ SecurityConfig is being initialized!");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("✅ Configuring SecurityFilterChain...");

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Preflight CORS
                        .requestMatchers("/csrf-token").permitAll()             // Allow CSRF token fetch
                        .requestMatchers("/auth/**").permitAll()                // Auth endpoints
                        .requestMatchers("/insights/**").authenticated()    // Insecure insight endpoints, fix soon
                        .anyRequest().authenticated()                           // Secure all else
                )

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/auth/login"),
                                new AntPathRequestMatcher("/auth/signup"),
                                new AntPathRequestMatcher("/csrf-token"),
                                new AntPathRequestMatcher("/insights/test_ping", "POST")
                        )
                        .csrfTokenRepository(csrfTokenRepository()) // auto sets the cookie
                )
//                                new AntPathRequestMatcher("/insights/test_ping", "POST")
//                .csrf(csrf -> csrf.disable())

                .addFilterBefore(new CsrfDebugFilter(), CsrfFilter.class) // 🐞 Add CSRF Debug Logger

                .sessionManagement(session -> session
                        .sessionFixation().none()
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )

                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .anonymous(anonymous -> anonymous.disable());

        return http.build();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repo.setHeaderName("X-XSRF-TOKEN"); // 🔥 Important!
        return repo;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-XSRF-TOKEN"));
        config.setExposedHeaders(List.of("X-CSRF-TOKEN"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 🐞 CSRF Debug Logger Filter
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