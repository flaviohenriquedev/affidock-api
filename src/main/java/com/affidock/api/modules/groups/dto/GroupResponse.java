package com.affidock.api.modules.groups.dto;

import com.affidock.api.common.domain.EntityStatus;
import java.time.Instant;
import java.util.UUID;

public record GroupResponse(
    UUID id,
    EntityStatus status,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    String name,
    String brandHex,
    String iconSlug,
    int productCount
) {
}
