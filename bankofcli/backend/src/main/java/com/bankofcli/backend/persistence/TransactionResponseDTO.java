package com.bankofcli.backend.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bankofcli.backend.domain.TransactionType;

public class TransactionResponseDTO {

    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final BigDecimal amount;
	private final String description;
    
    public TransactionResponseDTO(LocalDateTime timestamp, TransactionType type, BigDecimal amount, String description) {
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
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

    public String getDescription() {
        return description;
    }

}
