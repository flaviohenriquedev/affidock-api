package com.affidock.api.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GoogleExchangeRequest(
    @Email(message = "users.validation.email.invalid")
    @NotBlank(message = "users.validation.email.required")
    String email,
    @NotBlank(message = "users.validation.name.required")
    String name,
    String picture,
    String googleSubject
) {
}
