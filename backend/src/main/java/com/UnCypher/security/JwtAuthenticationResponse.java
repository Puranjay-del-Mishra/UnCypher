package com.UnCypher.security;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String refreshToken;

    public JwtAuthenticationResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
