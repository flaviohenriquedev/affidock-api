package com.affidock.api.common.exception;

import com.affidock.api.common.i18n.MessageResolver;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageResolver messageResolver;

    public GlobalExceptionHandler(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getCode(), ex.getSeverity(), List.of());
    }

    @ExceptionHandler(WarningException.class)
    public ResponseEntity<ApiErrorResponse> handleWarning(WarningException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getCode(), ex.getSeverity(), List.of());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getCode(), ex.getSeverity(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .toList();

        return ResponseEntity.badRequest().body(
            new ApiErrorResponse(
                "common.validation.invalid",
                messageResolver.resolve("common.validation.invalid"),
                ErrorSeverity.ERROR,
                details,
                OffsetDateTime.now()
            )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "common.internal.error", ErrorSeverity.ERROR, List.of(ex.getMessage()));
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, ErrorSeverity severity, List<String> details) {
        return ResponseEntity.status(status).body(
            new ApiErrorResponse(
                code,
                messageResolver.resolve(code),
                severity,
                details,
                OffsetDateTime.now()
            )
        );
    }
}
