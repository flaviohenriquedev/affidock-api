package com.affidock.api.modules.groups.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GroupRequest(
    @NotBlank(message = "groups.validation.name.required")
    String name,
    @NotBlank(message = "groups.validation.brandHex.required")
    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "groups.validation.brandHex.invalid")
    String brandHex,
    /** Opcional se houver imagem de capa; vazio = sem icone Simple Icons. */
    @Pattern(regexp = "^[a-z0-9-]*$", message = "groups.validation.iconSlug.invalid")
    @Size(max = 80, message = "groups.validation.iconSlug.length")
    String iconSlug,
    @Size(max = 1200, message = "groups.validation.coverImageUrl.length")
    String coverImageUrl
) {
}
