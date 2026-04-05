package com.example.finance_dashboard.DTO.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(example = "patidar29tanish@gmail.com")
        String email,
        @Schema(example = "123456789")
        String password
) {
}
