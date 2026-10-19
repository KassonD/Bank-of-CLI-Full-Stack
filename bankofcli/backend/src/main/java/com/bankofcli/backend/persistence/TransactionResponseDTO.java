package com.bankofcli.backend.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bankofcli.backend.domain.TransactionType;

public class TransactionResponseDTO {

    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final BigDecimal amount;
    private final int accountId;
    private final int relatedAccountId;
	private final String description;
    
    public TransactionResponseDTO(LocalDateTime timestamp, TransactionType type, BigDecimal amount, int accountId,  int relatedAccountId, String description) {
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.accountId = accountId;
        this.relatedAccountId = relatedAccountId;
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getRelatedAccountId() {
        return relatedAccountId;
    }

    public String getDescription() {
        return description;
    }

}
