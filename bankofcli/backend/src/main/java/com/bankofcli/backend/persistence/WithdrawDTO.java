package com.bankofcli.backend.persistence;

import java.math.BigDecimal;

public class WithdrawDTO {

    private BigDecimal amount;
    private int accountId;
    
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
}
