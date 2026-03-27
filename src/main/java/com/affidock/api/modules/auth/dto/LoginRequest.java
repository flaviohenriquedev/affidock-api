package com.affidock.api.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Email(message = "users.validation.email.invalid")
    @NotBlank(message = "users.validation.email.required")
    String email,
    @NotBlank(message = "auth.validation.password.required")
    String password
) {
}
