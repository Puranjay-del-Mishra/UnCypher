package com.UnCypher.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                // ✅ 1️⃣ Ensure OPTIONS Preflight Requests Are Fully Allowed
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // ✅ Allow OPTIONS preflight requests
                        .requestMatchers("/csrf-token").permitAll()  // ✅ Allow CSRF token requests
                        .requestMatchers("/auth/login", "/auth/register").permitAll() // ✅ Other public endpoints
                        .anyRequest().authenticated() // ✅ Protect all other endpoints
                )

                // ✅ 2️⃣ Enable CORS Before Security Filters
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ 3️⃣ Enable CSRF, But Ignore OPTIONS Requests & CSRF Token Fetching
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // ✅ CSRF Token in Cookie
                        .ignoringRequestMatchers("/csrf-token") // ✅ Ignore CSRF for CSRF token retrieval
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/**", "OPTIONS")) // ✅ Ignore CSRF for OPTIONS preflight requests
                )

                // ✅ 4️⃣ Disable Default Login Redirects & Authentication Filters for OPTIONS Requests
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ✅ Stateless sessions
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // ✅ Disable HTTP Basic Auth (Prevents Authentication for OPTIONS)
                .formLogin(formLogin -> formLogin.disable()) // ✅ Disable Form Login (Prevents Redirects)
                .anonymous(anonymous -> anonymous.disable()); // ✅ Ensure OPTIONS requests are not authenticated

        return http.build();
    }

    // ✅ 5️⃣ Global CORS Configuration to Allow Frontend Access
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("✅ Configuring CORS settings...");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // ✅ Allow cookies/auth headers
        config.setAllowedOrigins(allowedOrigins); // ✅ Use injected list of allowed origins
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // ✅ Allow HTTP methods
        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-CSRF-TOKEN", "X-XSRF-TOKEN", "x_csrf_token", "x_csrf_token")); // ✅ Allowed request headers
        config.setExposedHeaders(List.of("X-CSRF-TOKEN")); // ✅ Expose CSRF token
        config.setMaxAge(3600L); // ✅ Cache CORS for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}







