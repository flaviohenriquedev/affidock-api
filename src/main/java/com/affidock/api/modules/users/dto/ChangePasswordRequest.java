package com.affidock.api.modules.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "auth.validation.password.current.required")
    String currentPassword,
    @NotBlank(message = "auth.validation.password.required")
    @Size(min = 8, message = "auth.validation.password.min-length")
    String newPassword
) {
}
