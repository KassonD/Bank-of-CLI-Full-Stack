package com.bankofcli.backend.controller;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bankofcli.backend.dto.ErrorResponse;
import com.bankofcli.backend.exception.AccountNotFoundException;
import com.bankofcli.backend.exception.InsufficientFundsException;
import com.bankofcli.backend.exception.InvalidTransferAmountException;
import com.bankofcli.backend.exception.SameAccountTransferException;
import com.bankofcli.backend.exception.TransferException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Maps transfer failures to HTTP responses.
 *
 * Status codes:
 *   400 INVALID_AMOUNT / SAME_ACCOUNT_TRANSFER / VALIDATION_FAILED / MALFORMED_REQUEST
 *   401 UNAUTHENTICATED
 *   404 ACCOUNT_NOT_FOUND
 *   409 CONCURRENT_UPDATE (lock timeout; the client can retry)
 *   422 INSUFFICIENT_FUNDS (the request is well-formed but breaks a banking rule)
 */
@RestControllerAdvice(assignableTypes = TransferController.class)
public class TransferExceptionHandler {

    private final Clock clock;

    public TransferExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ErrorResponse> handleTransferException(TransferException ex, HttpServletRequest req) {
        return build(statusFor(ex), ex.getCode(), ex.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request validation failed", req, fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body is missing or malformed", req, null);
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleLockFailure(PessimisticLockingFailureException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "CONCURRENT_UPDATE",
                "The account is busy with another transaction. Please try again.", req, null);
    }

    private static HttpStatus statusFor(TransferException ex) {
        if (ex instanceof AccountNotFoundException) return HttpStatus.NOT_FOUND;
        if (ex instanceof InsufficientFundsException) return HttpStatus.UNPROCESSABLE_ENTITY;
        if (ex instanceof SameAccountTransferException
                || ex instanceof InvalidTransferAmountException) return HttpStatus.BAD_REQUEST;
        return HttpStatus.BAD_REQUEST;
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message,
                                                HttpServletRequest req, Map<String, String> fieldErrors) {
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(clock), status.value(), code, message, req.getRequestURI(), fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
