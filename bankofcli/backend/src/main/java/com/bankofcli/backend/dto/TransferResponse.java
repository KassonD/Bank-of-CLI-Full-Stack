package com.bankofcli.backend.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Returned on a successful transfer (201 Created).
 * Field names follow the spec's transaction.json (id, timestamp, type, amount,
 * account_id, related_account_id) in camelCase. newBalance lets the dashboard
 * refresh without a second request. Confirm the final names with Sylvie.
 */
public record TransferResponse(
        int id,
        OffsetDateTime timestamp,
        String type,
        BigDecimal amount,
        int accountId,
        int relatedAccountId,
        BigDecimal newBalance
) {
}
