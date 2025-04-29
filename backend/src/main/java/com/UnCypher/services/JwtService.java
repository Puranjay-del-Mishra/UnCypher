package com.UnCypher.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final Key secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setAllowedClockSkewSeconds(60) // <-- ⚠️ allow 60s drift
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            System.out.println("❌ JWT verification failed: " + ex.getMessage());
            return false;
        }
    }

    public String extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setAllowedClockSkewSeconds(60) // <-- ⚠️ apply here too
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 🆕 Optional: Token generator
    public String generateToken(String userId, Duration expiry) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiry.toMillis());

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
    }
}


