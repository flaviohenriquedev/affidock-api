package com.affidock.api.common.exception;

public class ApiException extends RuntimeException {
    private final String code;
    private final ErrorSeverity severity;

    public ApiException(String code, ErrorSeverity severity) {
        super(code);
        this.code = code;
        this.severity = severity;
    }

    public String getCode() {
        return code;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }
}
