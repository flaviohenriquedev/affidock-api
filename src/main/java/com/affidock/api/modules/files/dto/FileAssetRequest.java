package com.affidock.api.modules.files.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record FileAssetRequest(
    @NotBlank(message = "files.validation.storageProvider.required")
    String storageProvider,
    @NotBlank(message = "files.validation.objectKey.required")
    String objectKey,
    @NotBlank(message = "files.validation.publicUrl.required")
    String publicUrl,
    @NotBlank(message = "files.validation.mimeType.required")
    String mimeType,
    @Min(value = 0, message = "files.validation.sizeBytes.invalid")
    long sizeBytes,
    @NotBlank(message = "files.validation.originalName.required")
    String originalName
) {
}
