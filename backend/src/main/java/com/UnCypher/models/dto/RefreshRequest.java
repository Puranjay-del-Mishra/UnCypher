package com.UnCypher.models.dto;

public class RefreshRequest {
    private String refreshToken;

    public RefreshRequest() {} // Needed for @RequestBody

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
