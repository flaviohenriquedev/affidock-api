package com.affidock.api.modules.files.controller;

import com.affidock.api.common.base.BaseController;
import com.affidock.api.modules.files.dto.FileAssetRequest;
import com.affidock.api.modules.files.dto.FileAssetResponse;
import com.affidock.api.modules.files.dto.PublicImageUploadRequest;
import com.affidock.api.modules.files.dto.PublicImageUploadResponse;
import com.affidock.api.modules.files.service.FileAssetService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/files")
public class FileAssetController extends BaseController<FileAssetRequest, FileAssetResponse> {
    private final FileAssetService fileAssetService;

    public FileAssetController(FileAssetService service) {
        super(service);
        this.fileAssetService = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<PublicImageUploadResponse> uploadPublicImage(@Valid @RequestBody PublicImageUploadRequest request) {
        return ResponseEntity.ok(fileAssetService.uploadPublicImage(request));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<byte[]> getPublicFile(@PathVariable UUID id) {
        FileAssetService.FileBinaryData file = fileAssetService.getPublicFile(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
            .contentType(MediaType.parseMediaType(file.mimeType()))
            .body(file.bytes());
    }
}
