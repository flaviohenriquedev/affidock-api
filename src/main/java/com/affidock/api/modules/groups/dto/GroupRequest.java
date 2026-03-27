package com.affidock.api.modules.groups.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GroupRequest(
    @NotBlank(message = "groups.validation.name.required")
    String name,
    @NotBlank(message = "groups.validation.brandHex.required")
    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "groups.validation.brandHex.invalid")
    String brandHex,
    @NotBlank(message = "groups.validation.iconSlug.required")
    String iconSlug
) {
}
