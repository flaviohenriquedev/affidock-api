package com.affidock.api.modules.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AvatarUploadRequest(
    @NotBlank(message = "users.validation.avatar.base64.required")
    String base64Data,
    @NotBlank(message = "users.validation.avatar.mimeType.required")
    String mimeType,
    @Size(max = 240, message = "users.validation.avatar.fileName.length")
    String fileName
) {
}
