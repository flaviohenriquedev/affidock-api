package com.affidock.api.modules.files.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicImageUploadRequest(
    @NotBlank(message = "files.validation.upload.base64.required")
    String base64Data,
    @NotBlank(message = "files.validation.upload.mimeType.required")
    String mimeType,
    @Size(max = 240, message = "files.validation.upload.fileName.length")
    String fileName
) {
}
