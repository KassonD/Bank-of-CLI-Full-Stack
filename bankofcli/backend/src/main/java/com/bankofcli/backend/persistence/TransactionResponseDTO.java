package com.bankofcli.backend.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bankofcli.backend.domain.TransactionType;

public class TransactionResponseDTO {
    private final Integer transactionId;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Integer accountId;
    private final Integer relatedAccountId;
	private final String description;

    public TransactionResponseDTO(Integer transactionId, LocalDateTime timestamp, TransactionType type, 
            BigDecimal amount, Integer accountId, Integer relatedAccountId, String description) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.accountId = accountId;
        this.relatedAccountId = relatedAccountId;
        this.description = description;
    }

    public Integer getTransactionId() {
        return transactionId;
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

    public Integer getAccountId() {
        return accountId;
    }

    public Integer getRelatedAccountId() {
        return relatedAccountId;
    }

    public String getDescription() {
        return description;
    }
    
}
