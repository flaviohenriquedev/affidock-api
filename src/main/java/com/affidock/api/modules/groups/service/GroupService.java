package com.affidock.api.modules.groups.service;

import com.affidock.api.common.base.BaseService;
import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.modules.groups.domain.GroupEntity;
import com.affidock.api.modules.groups.dto.GroupRequest;
import com.affidock.api.modules.groups.dto.GroupResponse;
import com.affidock.api.modules.groups.repository.GroupRepository;
import com.affidock.api.modules.products.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupService extends BaseService<GroupEntity, GroupRequest, GroupResponse> {
    private final ProductRepository productRepository;

    public GroupService(GroupRepository repository, ProductRepository productRepository) {
        super(repository, "groups.notfound");
        this.productRepository = productRepository;
    }

    @Override
    protected GroupEntity toEntity(GroupRequest request) {
        GroupEntity entity = new GroupEntity();
        entity.setName(request.name());
        entity.setBrandHex(request.brandHex());
        entity.setIconSlug(request.iconSlug());
        entity.setCoverImageUrl(normalizeNullable(request.coverImageUrl()));
        return entity;
    }

    @Override
    protected void updateEntity(GroupEntity entity, GroupRequest request) {
        entity.setName(request.name());
        entity.setBrandHex(request.brandHex());
        entity.setIconSlug(request.iconSlug());
        entity.setCoverImageUrl(normalizeNullable(request.coverImageUrl()));
    }

    @Override
    protected GroupResponse toResponse(GroupEntity entity) {
        long productCount = productRepository.countByGroupIdAndStatusNot(entity.getId(), EntityStatus.DELETADO);
        return new GroupResponse(
            entity.getId(),
            entity.getStatus(),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedBy(),
            entity.getUpdatedAt(),
            entity.getName(),
            entity.getBrandHex(),
            entity.getIconSlug(),
            entity.getCoverImageUrl(),
            Math.toIntExact(productCount)
        );
    }

    private String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
