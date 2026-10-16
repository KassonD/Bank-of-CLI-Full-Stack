package com.bankofcli.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Body of POST /api/transfers.
 * There is deliberately no "fromAccountId": the sender always comes from the
 * JWT, so a logged-in user cannot move money out of someone else's account.
 */
public record TransferRequest(

        @NotNull(message = "toAccountId is required")
        @Positive(message = "toAccountId must be a positive number")
        Integer toAccountId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
        @Digits(integer = 13, fraction = 2, message = "amount must have at most 2 decimal places")
        BigDecimal amount
) {
}
