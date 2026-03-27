package com.affidock.api.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetupPasswordRequest(
    @Email(message = "users.validation.email.invalid")
    @NotBlank(message = "users.validation.email.required")
    String email,
    @NotBlank(message = "auth.validation.password.required")
    @Size(min = 8, message = "auth.validation.password.min-length")
    String password
) {
}
