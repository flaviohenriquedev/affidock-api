package com.affidock.api.common.base;

import java.util.List;
import java.util.UUID;

public interface BaseCrudService<REQ, RES> {
    RES create(REQ request);

    RES findById(UUID id);

    List<RES> findAll();

    RES update(UUID id, REQ request);

    void delete(UUID id);
}
