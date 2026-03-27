package com.affidock.api.common.base;

import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.common.exception.NotFoundException;
import java.util.List;
import java.util.UUID;

public abstract class BaseService<T extends BaseAuditableEntity, REQ, RES> implements BaseCrudService<REQ, RES> {

    private final BaseRepository<T> repository;
    private final String notFoundCode;

    protected BaseService(BaseRepository<T> repository, String notFoundCode) {
        this.repository = repository;
        this.notFoundCode = notFoundCode;
    }

    @Override
    public RES create(REQ request) {
        T entity = toEntity(request);
        entity.setId(UUID.randomUUID());
        entity.setStatus(EntityStatus.ATIVO);
        return toResponse(repository.save(entity));
    }

    @Override
    public RES findById(UUID id) {
        return toResponse(getExistingOrThrow(id));
    }

    @Override
    public List<RES> findAll() {
        return repository.findAllByStatusNot(EntityStatus.DELETADO, org.springframework.data.domain.Pageable.unpaged())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public RES update(UUID id, REQ request) {
        T entity = getExistingOrThrow(id);
        updateEntity(entity, request);
        return toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        T entity = getExistingOrThrow(id);
        entity.setStatus(EntityStatus.DELETADO);
        repository.save(entity);
    }

    protected T getExistingOrThrow(UUID id) {
        return repository.findByIdAndStatusNot(id, EntityStatus.DELETADO)
            .orElseThrow(() -> new NotFoundException(notFoundCode));
    }

    protected abstract T toEntity(REQ request);

    protected abstract void updateEntity(T entity, REQ request);

    protected abstract RES toResponse(T entity);
}
