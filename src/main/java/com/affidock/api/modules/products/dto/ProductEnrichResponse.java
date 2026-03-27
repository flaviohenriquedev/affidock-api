package com.affidock.api.modules.products.dto;

import java.util.List;

public record ProductEnrichResponse(
    String name,
    String imageUrl,
    String producerName,
    Long originalPriceCents,
    Long salePriceCents,
    String source,
    List<String> warnings
) {
}
