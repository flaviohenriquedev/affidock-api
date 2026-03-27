package com.affidock.api.modules.products.repository;

import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.common.base.BaseRepository;
import com.affidock.api.modules.products.domain.ProductEntity;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends BaseRepository<ProductEntity> {
    List<ProductEntity> findByGroupId(UUID groupId);
    long countByGroupIdAndStatusNot(UUID groupId, EntityStatus status);
}
