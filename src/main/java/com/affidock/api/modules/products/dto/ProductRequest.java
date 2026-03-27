package com.affidock.api.modules.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ProductRequest(
    @NotNull(message = "products.validation.groupId.required")
    UUID groupId,
    @NotBlank(message = "products.validation.name.required")
    String name,
    @NotBlank(message = "products.validation.accentHex.required")
    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "products.validation.accentHex.invalid")
    String accentHex,
    @NotBlank(message = "products.validation.affiliateUrl.required")
    String affiliateUrl,
    @NotBlank(message = "products.validation.imageUrl.required")
    @Size(max = 1200, message = "products.validation.imageUrl.length")
    String imageUrl,
    @NotBlank(message = "products.validation.producerName.required")
    @Size(max = 180, message = "products.validation.producerName.length")
    String producerName,
    @PositiveOrZero(message = "products.validation.originalPriceCents.invalid")
    Long originalPriceCents,
    @NotNull(message = "products.validation.salePriceCents.required")
    @PositiveOrZero(message = "products.validation.salePriceCents.invalid")
    Long salePriceCents
) {
}
