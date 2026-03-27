package com.affidock.api.modules.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank(message = "users.validation.name.required")
    String name,
    @Email(message = "users.validation.email.invalid")
    @NotBlank(message = "users.validation.email.required")
    String email,
    String avatarUrl
) {
}
