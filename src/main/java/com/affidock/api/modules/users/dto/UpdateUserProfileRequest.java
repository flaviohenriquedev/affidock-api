package com.affidock.api.modules.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
    @NotBlank(message = "users.validation.name.required")
    @Size(max = 160, message = "users.validation.name.length")
    String name,
    @Size(max = 120, message = "users.validation.sharedSlug.length")
    String sharedSlug,
    @Size(max = 30, message = "users.validation.phone.length")
    @Pattern(regexp = "^$|^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "users.validation.phone.invalid")
    String phone,
    @Size(max = 30, message = "users.validation.whatsapp.length")
    @Pattern(regexp = "^$|^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "users.validation.whatsapp.invalid")
    String whatsapp,
    @Size(max = 160, message = "users.validation.secondaryEmail.length")
    @Pattern(regexp = "^$|^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "users.validation.secondaryEmail.invalid")
    String secondaryEmail,
    @Size(max = 300, message = "users.validation.linkedinUrl.length")
    @Pattern(regexp = "^$|^https?://.+$", message = "users.validation.linkedinUrl.invalid")
    String linkedinUrl,
    @Size(max = 300, message = "users.validation.websiteUrl.length")
    @Pattern(regexp = "^$|^https?://.+$", message = "users.validation.websiteUrl.invalid")
    String websiteUrl,
    @Size(max = 500, message = "users.validation.bio.length")
    String bio,
    @Size(max = 20, message = "users.validation.themePreference.length")
    String themePreference
) {
}
