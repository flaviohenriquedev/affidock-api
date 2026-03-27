package com.affidock.api.modules.products.service;

import com.affidock.api.common.base.BaseService;
import com.affidock.api.common.exception.NotFoundException;
import com.affidock.api.modules.groups.domain.GroupEntity;
import com.affidock.api.modules.groups.repository.GroupRepository;
import com.affidock.api.modules.products.domain.ProductEntity;
import com.affidock.api.modules.products.dto.ProductRequest;
import com.affidock.api.modules.products.dto.ProductResponse;
import com.affidock.api.modules.products.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends BaseService<ProductEntity, ProductRequest, ProductResponse> {

    private final GroupRepository groupRepository;

    public ProductService(ProductRepository repository, GroupRepository groupRepository) {
        super(repository, "products.notfound");
        this.groupRepository = groupRepository;
    }

    @Override
    protected ProductEntity toEntity(ProductRequest request) {
        ProductEntity entity = new ProductEntity();
        entity.setName(request.name());
        entity.setAccentHex(request.accentHex());
        entity.setAffiliateUrl(request.affiliateUrl());
        entity.setGroup(getGroup(request.groupId()));
        return entity;
    }

    @Override
    protected void updateEntity(ProductEntity entity, ProductRequest request) {
        entity.setName(request.name());
        entity.setAccentHex(request.accentHex());
        entity.setAffiliateUrl(request.affiliateUrl());
        entity.setGroup(getGroup(request.groupId()));
    }

    @Override
    protected ProductResponse toResponse(ProductEntity entity) {
        return new ProductResponse(
            entity.getId(),
            entity.getStatus(),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedBy(),
            entity.getUpdatedAt(),
            entity.getGroup().getId(),
            entity.getName(),
            entity.getAccentHex(),
            entity.getAffiliateUrl()
        );
    }

    private GroupEntity getGroup(java.util.UUID groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("groups.notfound"));
    }
}
