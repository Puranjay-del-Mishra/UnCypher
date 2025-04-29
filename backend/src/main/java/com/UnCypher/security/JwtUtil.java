package com.UnCypher.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey key;

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15;         // 15 minutes
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7 days
    private final long SERVICE_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30; // 30 days (for service tokens)

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    // === Normal user token generation ===
    public String generateAccessToken(String userId, UserDetails userDetails) {
        return generateToken(userId, userDetails, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(String userId, UserDetails userDetails) {
        return generateToken(userId, userDetails, REFRESH_TOKEN_VALIDITY);
    }

    private String generateToken(String userId, UserDetails userDetails, long expirationMs) {
        System.out.println("🛠 [JwtUtil] Generating user token for userId: " + userId);
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    // === NEW: Service token generation ===
    public String generateServiceToken(String serviceName) {
        System.out.println("🛠 [JwtUtil] Generating service token for: " + serviceName);
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", Collections.singletonList("ROLE_AGENT"));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(serviceName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + SERVICE_TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }

    // === Validation and parsing ===
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        return userId.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setAllowedClockSkewSeconds(300)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


