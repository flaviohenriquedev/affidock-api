package com.affidock.api.modules.files.service;

import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.common.exception.WarningException;
import com.affidock.api.common.base.BaseService;
import com.affidock.api.modules.files.domain.FileAssetEntity;
import com.affidock.api.modules.files.dto.FileAssetRequest;
import com.affidock.api.modules.files.dto.FileAssetResponse;
import com.affidock.api.modules.files.dto.PublicImageUploadRequest;
import com.affidock.api.modules.files.dto.PublicImageUploadResponse;
import com.affidock.api.modules.files.repository.FileAssetRepository;
import com.affidock.api.modules.users.domain.UserEntity;
import com.affidock.api.modules.users.repository.UserRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class FileAssetService extends BaseService<FileAssetEntity, FileAssetRequest, FileAssetResponse> {
    private static final Path STORAGE_ROOT = Paths.get("storage");
    private final FileAssetRepository fileAssetRepository;
    private final UserRepository userRepository;

    public FileAssetService(FileAssetRepository repository, UserRepository userRepository) {
        super(repository, "files.notfound");
        this.fileAssetRepository = repository;
        this.userRepository = userRepository;
    }

    @Override
    protected FileAssetEntity toEntity(FileAssetRequest request) {
        FileAssetEntity entity = new FileAssetEntity();
        entity.setStorageProvider(request.storageProvider());
        entity.setObjectKey(request.objectKey());
        entity.setPublicUrl(request.publicUrl());
        entity.setMimeType(request.mimeType());
        entity.setSizeBytes(request.sizeBytes());
        entity.setOriginalName(request.originalName());
        return entity;
    }

    @Override
    protected void updateEntity(FileAssetEntity entity, FileAssetRequest request) {
        entity.setStorageProvider(request.storageProvider());
        entity.setObjectKey(request.objectKey());
        entity.setPublicUrl(request.publicUrl());
        entity.setMimeType(request.mimeType());
        entity.setSizeBytes(request.sizeBytes());
        entity.setOriginalName(request.originalName());
    }

    @Override
    protected FileAssetResponse toResponse(FileAssetEntity entity) {
        return new FileAssetResponse(
            entity.getId(),
            entity.getStatus(),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedBy(),
            entity.getUpdatedAt(),
            entity.getStorageProvider(),
            entity.getObjectKey(),
            entity.getPublicUrl(),
            entity.getMimeType(),
            entity.getSizeBytes(),
            entity.getOriginalName()
        );
    }

    public FileBinaryData getPublicFile(UUID id) {
        FileAssetEntity asset = fileAssetRepository.findByIdAndStatusNot(id, EntityStatus.DELETADO)
            .orElseThrow(() -> new WarningException("files.notfound"));
        try {
            Path filePath = STORAGE_ROOT.resolve(asset.getObjectKey());
            byte[] bytes = Files.readAllBytes(filePath);
            return new FileBinaryData(bytes, asset.getMimeType());
        } catch (Exception exception) {
            throw new WarningException("files.notfound");
        }
    }

    @Transactional
    public PublicImageUploadResponse uploadPublicImage(PublicImageUploadRequest request) {
        UserEntity user = getAuthenticatedUserEntity();
        try {
            String sanitizedMime = normalizeImageMimeType(request.mimeType());
            byte[] content = java.util.Base64.getDecoder().decode(request.base64Data());
            String extension = extensionFromMimeType(sanitizedMime);
            String objectKey = "uploads/" + user.getId() + "/" + UUID.randomUUID() + "." + extension;
            Path outputPath = STORAGE_ROOT.resolve(objectKey);
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, content);

            FileAssetEntity asset = new FileAssetEntity();
            asset.setId(UUID.randomUUID());
            asset.setStatus(EntityStatus.ATIVO);
            asset.setStorageProvider("SIMULATED_AWS_S3");
            asset.setObjectKey(objectKey.replace('\\', '/'));
            asset.setPublicUrl("pending");
            asset.setMimeType(sanitizedMime);
            asset.setSizeBytes(content.length);
            asset.setOriginalName(
                StringUtils.hasText(request.fileName()) ? request.fileName() : "upload." + extension
            );
            FileAssetEntity saved = fileAssetRepository.save(asset);
            saved.setPublicUrl("/api/v1/files/public/" + saved.getId());
            fileAssetRepository.save(saved);
            return new PublicImageUploadResponse(saved.getId(), saved.getPublicUrl());
        } catch (IllegalArgumentException exception) {
            throw new WarningException("files.validation.upload.invalid");
        } catch (Exception exception) {
            throw new RuntimeException("Erro ao salvar arquivo.", exception);
        }
    }

    private UserEntity getAuthenticatedUserEntity() {
        String email = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(Authentication::getName)
            .filter(name -> !name.isBlank())
            .orElseThrow(() -> new WarningException("auth.session.invalid"));
        return userRepository.findByEmail(email).orElseThrow(() -> new WarningException("auth.user.not.registered"));
    }

    private String normalizeImageMimeType(String mimeType) {
        if (mimeType == null) return "image/png";
        if (mimeType.equals("image/jpeg") || mimeType.equals("image/png") || mimeType.equals("image/webp")) {
            return mimeType;
        }
        throw new WarningException("files.validation.upload.invalid");
    }

    private String extensionFromMimeType(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg" -> "jpg";
            case "image/webp" -> "webp";
            default -> "png";
        };
    }

    public record FileBinaryData(byte[] bytes, String mimeType) {}
}
