package com.bankofcli.backend.persistence;

import java.math.BigDecimal;

public class TransferDTO {

    private BigDecimal amount;
    private int accountId;
    private int relatedAccountId;

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
    public int getRelatedAccountId() {
        return relatedAccountId;
    }
    public void setRelatedAccountId(int relatedAccountId) {
        this.relatedAccountId = relatedAccountId;
    }
}
