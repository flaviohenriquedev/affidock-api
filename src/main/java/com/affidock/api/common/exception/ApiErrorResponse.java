package com.affidock.api.common.exception;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiErrorResponse(
    String code,
    String message,
    ErrorSeverity severity,
    List<String> details,
    OffsetDateTime timestamp
) {
}
