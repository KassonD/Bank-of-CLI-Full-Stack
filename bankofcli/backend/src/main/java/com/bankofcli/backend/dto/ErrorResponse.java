package com.bankofcli.backend.dto;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Error body for the transfer endpoint. "code" is a stable, machine-readable
 * value the frontend can switch on (for example INSUFFICIENT_FUNDS), so the UI never 
 * has to parse the human-readable message. fieldErrors is only filled for validation failures.
 */
public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
