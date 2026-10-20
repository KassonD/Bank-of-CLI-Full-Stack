package com.bankofcli.backend.persistence;

import java.math.BigDecimal;

import com.bankofcli.backend.domain.TransactionType;

public class TransactionWriteDTO {

    private TransactionType type;
    private BigDecimal amount;
    private Integer accountId;
    private Integer relatedAccountId;
	private String description;

	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Integer getRelatedAccountId() {
		return relatedAccountId;
	}
	public void setRelatedAccountId(Integer relatedAccountId) {
		this.relatedAccountId = relatedAccountId;
	}
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
}
