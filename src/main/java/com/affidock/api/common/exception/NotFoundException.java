package com.affidock.api.common.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(String code) {
        super(code, ErrorSeverity.ERROR);
    }
}
