package com.affidock.api.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
    @NotBlank(message = "common.validation.required")
    String refreshToken
) {
}
