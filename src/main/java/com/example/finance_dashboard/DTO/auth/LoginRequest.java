package com.example.finance_dashboard.DTO.auth;

public record LoginRequest(
        String email,
        String password
) {
}
