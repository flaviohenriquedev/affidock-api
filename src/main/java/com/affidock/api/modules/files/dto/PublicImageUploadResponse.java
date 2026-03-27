package com.affidock.api.modules.files.dto;

import java.util.UUID;

public record PublicImageUploadResponse(UUID id, String publicUrl) {
}
