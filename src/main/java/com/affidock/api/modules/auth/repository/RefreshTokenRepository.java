package com.affidock.api.modules.auth.repository;

import com.affidock.api.common.base.BaseRepository;
import com.affidock.api.modules.auth.domain.RefreshTokenEntity;
import java.util.Optional;

public interface RefreshTokenRepository extends BaseRepository<RefreshTokenEntity> {
    Optional<RefreshTokenEntity> findByTokenId(String tokenId);
}
