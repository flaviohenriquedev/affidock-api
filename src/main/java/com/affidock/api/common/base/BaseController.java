package com.affidock.api.common.base;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseController<REQ, RES> {

    /**
     * UUID only — avoids shadowing literal subpaths on the same base (e.g. {@code /enrich-from-url})
     * being mistaken for {@code /{id}}.
     */
    private static final String ID_SEGMENT =
        "{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}";

    private final BaseCrudService<REQ, RES> service;

    protected BaseController(BaseCrudService<REQ, RES> service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RES> create(@RequestBody REQ request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/" + ID_SEGMENT)
    public ResponseEntity<RES> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RES>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/" + ID_SEGMENT)
    public ResponseEntity<RES> update(@PathVariable UUID id, @RequestBody REQ request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/" + ID_SEGMENT)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
