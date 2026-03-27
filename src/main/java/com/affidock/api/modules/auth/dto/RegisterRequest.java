package com.affidock.api.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "auth.validation.name.required")
    @Size(max = 160, message = "users.validation.name.length")
    String name,
    @NotBlank(message = "auth.validation.email.required")
    @Email(message = "auth.validation.email.invalid")
    @Size(max = 160, message = "users.validation.email.length")
    String email,
    @NotBlank(message = "auth.validation.password.required")
    @Size(min = 8, message = "auth.validation.password.min-length")
    String password
) {
}
