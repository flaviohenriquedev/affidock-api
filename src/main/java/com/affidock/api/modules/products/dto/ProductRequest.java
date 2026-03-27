package com.affidock.api.modules.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    String affiliateUrl
) {
}
