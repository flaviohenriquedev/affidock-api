package com.affidock.api.common.base;

import com.affidock.api.common.domain.EntityStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends BaseAuditableEntity> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    Optional<T> findByIdAndStatusNot(UUID id, EntityStatus status);

    Page<T> findAllByStatusNot(EntityStatus status, Pageable pageable);
}
