package com.affidock.api.modules.files.service;

import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.common.exception.WarningException;
import com.affidock.api.common.base.BaseService;
import com.affidock.api.modules.files.domain.FileAssetEntity;
import com.affidock.api.modules.files.dto.FileAssetRequest;
import com.affidock.api.modules.files.dto.FileAssetResponse;
import com.affidock.api.modules.files.repository.FileAssetRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FileAssetService extends BaseService<FileAssetEntity, FileAssetRequest, FileAssetResponse> {
    private static final Path STORAGE_ROOT = Paths.get("storage");
    private final FileAssetRepository fileAssetRepository;

    public FileAssetService(FileAssetRepository repository) {
        super(repository, "files.notfound");
        this.fileAssetRepository = repository;
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

    public record FileBinaryData(byte[] bytes, String mimeType) {}
}
