package com.affidock.api.modules.auth.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn
) {
}
