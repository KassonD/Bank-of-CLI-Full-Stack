package com.bankofcli.backend.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

@Entity 
@Table (name = "transaction")
public class Transaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    public Account getAccount(){
        return account;
    }

    public void setAccount(Account account){
        this.account = account;
    }

//////////////////
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_account_id")
    private Account relatedAccount;

    public Account getRelatedAccount() {
        return relatedAccount;
    }

    public void setRelatedAccount(Account relatedAccount) {
        this.relatedAccount = relatedAccount;
    }
/////////////////

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

/////////////////
    // The mock timestamp is "2020-06-22 19:10:25-07". LocalDate drops the time and
    // offset, so history could not order two transfers made on the same day.
    // Maps to TIMESTAMP WITH TIME ZONE in PostgreSQL.
    @Column (name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;
/////////////////

    @Column (name = "type")
    private String type;

    @Column (name = "amount")
    private BigDecimal amount;

    public Transaction(OffsetDateTime timestamp, String type, BigDecimal amount) {
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    protected Transaction() {}

}
