package com.affidock.api.modules.products.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductEnrichRequest(
    @NotBlank(message = "products.validation.affiliateUrl.required")
    String affiliateUrl
) {
}
