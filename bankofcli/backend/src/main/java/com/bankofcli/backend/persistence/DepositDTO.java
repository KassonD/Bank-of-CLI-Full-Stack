package com.bankofcli.backend.persistence;

import java.math.BigDecimal;

public class DepositDTO {
    
    private BigDecimal amount;
    private int accountId;
	private String description;

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
	
}
