package com.affidock.api.modules.users.dto;

import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.modules.users.domain.AuthProvider;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    EntityStatus status,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    String name,
    String email,
    String avatarUrl,
    UUID avatarFileId,
    AuthProvider provider
) {
}
