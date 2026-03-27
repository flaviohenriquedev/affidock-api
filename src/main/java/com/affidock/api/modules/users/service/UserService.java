package com.affidock.api.modules.users.service;

import com.affidock.api.common.base.BaseService;
import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.common.exception.WarningException;
import com.affidock.api.modules.files.domain.FileAssetEntity;
import com.affidock.api.modules.files.repository.FileAssetRepository;
import com.affidock.api.modules.users.domain.AuthProvider;
import com.affidock.api.modules.users.domain.UserEntity;
import com.affidock.api.modules.users.dto.UpdateUserProfileRequest;
import com.affidock.api.modules.users.dto.UserProfileResponse;
import com.affidock.api.modules.users.dto.UserRequest;
import com.affidock.api.modules.users.dto.UserResponse;
import com.affidock.api.modules.users.repository.UserRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends BaseService<UserEntity, UserRequest, UserResponse> {
    private static final Path AVATAR_STORAGE_DIR = Paths.get("storage", "avatars");
    private static final Pattern SHARED_SLUG_PATTERN = Pattern.compile("^[a-z0-9-]{3,120}$");
    private final UserRepository userRepository;
    private final FileAssetRepository fileAssetRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository repository,
        FileAssetRepository fileAssetRepository,
        PasswordEncoder passwordEncoder
    ) {
        super(repository, "users.notfound");
        this.userRepository = repository;
        this.fileAssetRepository = fileAssetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected UserEntity toEntity(UserRequest request) {
        UserEntity entity = new UserEntity();
        entity.setName(request.name());
        entity.setEmail(request.email());
        entity.setAvatarUrl(request.avatarUrl());
        entity.setProvider(AuthProvider.LOCAL);
        return entity;
    }

    @Override
    protected void updateEntity(UserEntity entity, UserRequest request) {
        entity.setName(request.name());
        entity.setEmail(request.email());
        entity.setAvatarUrl(request.avatarUrl());
    }

    @Override
    protected UserResponse toResponse(UserEntity entity) {
        return new UserResponse(
            entity.getId(),
            entity.getStatus(),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedBy(),
            entity.getUpdatedAt(),
            entity.getName(),
            entity.getEmail(),
            entity.getAvatarUrl(),
            entity.getAvatarFileId(),
            entity.getProvider()
        );
    }

    @Transactional
    public void changeAuthenticatedUserPassword(String currentPassword, String newPassword) {
        UserEntity user = getAuthenticatedUserEntity();

        if (user.getPasswordHash() == null) {
            throw new WarningException("auth.password.current.missing");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new WarningException("auth.password.current.invalid");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getAuthenticatedUserProfile() {
        return toProfileResponse(getAuthenticatedUserEntity());
    }

    @Transactional
    public UserProfileResponse updateAuthenticatedUserProfile(UpdateUserProfileRequest request) {
        UserEntity user = getAuthenticatedUserEntity();
        String normalizedSharedSlug = normalizeSharedSlug(request.sharedSlug());
        if (normalizedSharedSlug != null) {
            boolean slugInUse = userRepository.existsBySharedSlugIgnoreCaseAndStatusNotAndIdNot(
                normalizedSharedSlug,
                EntityStatus.DELETADO,
                user.getId()
            );
            if (slugInUse) {
                throw new WarningException("users.validation.sharedSlug.duplicated");
            }
        }

        user.setName(request.name().trim());
        user.setSharedSlug(normalizedSharedSlug);
        user.setPhone(normalizeNullable(request.phone()));
        user.setWhatsapp(normalizeNullable(request.whatsapp()));
        user.setSecondaryEmail(normalizeNullable(request.secondaryEmail()));
        user.setLinkedinUrl(normalizeNullable(request.linkedinUrl()));
        user.setWebsiteUrl(normalizeNullable(request.websiteUrl()));
        user.setBio(normalizeNullable(request.bio()));
        user.setThemePreference(normalizeNullable(request.themePreference()));
        return toProfileResponse(userRepository.save(user));
    }

    @Transactional
    public UserProfileResponse deleteAuthenticatedUserAvatar() {
        UserEntity user = getAuthenticatedUserEntity();
        user.setAvatarFileId(null);
        user.setAvatarUrl(null);
        return toProfileResponse(userRepository.save(user));
    }

    @Transactional
    public UserProfileResponse uploadAuthenticatedUserAvatar(String base64Data, String mimeType, String fileName) {
        UserEntity user = getAuthenticatedUserEntity();
        try {
            String sanitizedMimeType = normalizeMimeType(mimeType);
            byte[] content = java.util.Base64.getDecoder().decode(base64Data);

            Files.createDirectories(AVATAR_STORAGE_DIR);
            String extension = extensionFromMimeType(sanitizedMimeType);
            String objectKey = "avatars/" + user.getId() + "/" + UUID.randomUUID() + "." + extension;
            Path outputPath = Paths.get("storage").resolve(objectKey);
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, content);

            FileAssetEntity asset = new FileAssetEntity();
            asset.setId(UUID.randomUUID());
            asset.setStatus(EntityStatus.ATIVO);
            asset.setStorageProvider("SIMULATED_AWS_S3");
            asset.setObjectKey(objectKey.replace('\\', '/'));
            asset.setPublicUrl("pending");
            asset.setMimeType(sanitizedMimeType);
            asset.setSizeBytes(content.length);
            asset.setOriginalName(StringUtils.hasText(fileName) ? fileName : "avatar." + extension);
            FileAssetEntity savedAsset = fileAssetRepository.save(asset);
            savedAsset.setPublicUrl("/api/v1/files/public/" + savedAsset.getId());
            fileAssetRepository.save(savedAsset);

            user.setAvatarFileId(savedAsset.getId());
            user.setAvatarUrl(savedAsset.getPublicUrl());
            return toProfileResponse(userRepository.save(user));
        } catch (IllegalArgumentException exception) {
            throw new WarningException("users.validation.avatar.invalid");
        } catch (Exception exception) {
            throw new RuntimeException("Erro ao salvar avatar.", exception);
        }
    }

    @Transactional(readOnly = true)
    public AvatarBinaryData getAuthenticatedUserAvatarBinary() {
        UserEntity user = getAuthenticatedUserEntity();
        if (user.getAvatarFileId() == null) {
            throw new WarningException("users.avatar.notfound");
        }
        FileAssetEntity asset = fileAssetRepository.findByIdAndStatusNot(user.getAvatarFileId(), EntityStatus.DELETADO)
            .orElseThrow(() -> new WarningException("users.avatar.notfound"));
        try {
            Path storedPath = Paths.get("storage").resolve(asset.getObjectKey());
            byte[] bytes = Files.readAllBytes(storedPath);
            return new AvatarBinaryData(bytes, asset.getMimeType());
        } catch (Exception exception) {
            throw new WarningException("users.avatar.notfound");
        }
    }

    private UserEntity getAuthenticatedUserEntity() {
        String authenticatedEmail = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(Authentication::getName)
            .filter(name -> !name.isBlank())
            .orElseThrow(() -> new WarningException("auth.session.invalid"));
        return userRepository.findByEmail(authenticatedEmail)
            .orElseThrow(() -> new WarningException("auth.user.not.registered"));
    }

    private UserProfileResponse toProfileResponse(UserEntity entity) {
        return new UserProfileResponse(
            entity.getId(),
            entity.getEmail(),
            entity.getName(),
            entity.getAvatarUrl(),
            entity.getAvatarFileId(),
            entity.getProvider(),
            entity.getSharedSlug(),
            entity.getPhone(),
            entity.getWhatsapp(),
            entity.getSecondaryEmail(),
            entity.getLinkedinUrl(),
            entity.getWebsiteUrl(),
            entity.getBio(),
            entity.getThemePreference(),
            entity.getUpdatedAt() != null ? entity.getUpdatedAt() : Instant.now()
        );
    }

    private String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeSharedSlug(String slug) {
        String normalized = normalizeNullable(slug);
        if (normalized == null) return null;
        String lower = normalized.toLowerCase();
        if (!SHARED_SLUG_PATTERN.matcher(lower).matches()) {
            throw new WarningException("users.validation.sharedSlug.invalid");
        }
        return lower;
    }

    private String normalizeMimeType(String mimeType) {
        if (mimeType == null) return "image/png";
        if (mimeType.equals("image/jpeg") || mimeType.equals("image/png") || mimeType.equals("image/webp")) {
            return mimeType;
        }
        throw new WarningException("users.validation.avatar.invalid");
    }

    private String extensionFromMimeType(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg" -> "jpg";
            case "image/webp" -> "webp";
            default -> "png";
        };
    }

    public record AvatarBinaryData(byte[] bytes, String mimeType) {}
}
