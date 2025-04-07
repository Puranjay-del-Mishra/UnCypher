package com.UnCypher.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    // 🔐 Use strong 256-bit secret for HMAC
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15;     // 15 mins
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7 days

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, REFRESH_TOKEN_VALIDITY);
    }

    private String generateToken(UserDetails userDetails, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();

        // Add roles as a claim
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
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
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
