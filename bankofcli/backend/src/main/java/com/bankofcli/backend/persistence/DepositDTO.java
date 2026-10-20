package com.bankofcli.backend.persistence;

import java.math.BigDecimal;

public class DepositDTO {
    
    private BigDecimal amount;
    private Integer accountId;
	private String description;

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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
	
}
