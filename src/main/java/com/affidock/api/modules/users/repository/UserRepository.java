package com.affidock.api.modules.users.repository;

import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.common.base.BaseRepository;
import com.affidock.api.modules.users.domain.UserEntity;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<UserEntity> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByGoogleSubject(String googleSubject);

    Optional<UserEntity> findById(UUID id);

    boolean existsBySharedSlugIgnoreCaseAndStatusNotAndIdNot(String sharedSlug, EntityStatus status, UUID id);
}
