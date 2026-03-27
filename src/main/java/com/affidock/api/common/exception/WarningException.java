package com.affidock.api.common.exception;

public class WarningException extends ApiException {
    public WarningException(String code) {
        super(code, ErrorSeverity.WARNING);
    }
}
