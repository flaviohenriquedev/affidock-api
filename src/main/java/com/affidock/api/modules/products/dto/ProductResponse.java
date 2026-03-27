package com.affidock.api.modules.products.dto;

import com.affidock.api.common.domain.EntityStatus;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    EntityStatus status,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    UUID groupId,
    String name,
    String accentHex,
    String affiliateUrl,
    String imageUrl,
    String producerName,
    Long originalPriceCents,
    Long salePriceCents
) {
}
