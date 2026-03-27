package com.affidock.api.modules.users.dto;

import com.affidock.api.modules.users.domain.AuthProvider;
import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
    UUID id,
    String email,
    String name,
    String avatarUrl,
    UUID avatarFileId,
    AuthProvider provider,
    String sharedSlug,
    String phone,
    String whatsapp,
    String secondaryEmail,
    String linkedinUrl,
    String websiteUrl,
    String bio,
    String themePreference,
    Instant updatedAt
) {
}
