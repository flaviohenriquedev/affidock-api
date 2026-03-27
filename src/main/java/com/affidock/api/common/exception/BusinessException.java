package com.affidock.api.common.exception;

public class BusinessException extends ApiException {
    public BusinessException(String code) {
        super(code, ErrorSeverity.ERROR);
    }
}
