package com.affidock.api.modules.files.dto;

import com.affidock.api.common.domain.EntityStatus;
import java.time.Instant;
import java.util.UUID;

public record FileAssetResponse(
    UUID id,
    EntityStatus status,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    String storageProvider,
    String objectKey,
    String publicUrl,
    String mimeType,
    long sizeBytes,
    String originalName
) {
}
