package com.affidock.api.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "common.validation.required")
    String refreshToken
) {
}
