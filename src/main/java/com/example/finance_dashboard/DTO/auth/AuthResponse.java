package com.example.finance_dashboard.DTO.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String email,
        String role
) {
}
