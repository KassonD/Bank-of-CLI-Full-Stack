package com.bankofcli.backend.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "timestamp")
    private LocalDateTime timestamp;

    @Enumerated (EnumType.STRING)
    @Column (name = "type")
    private TransactionType type;

    @Column (name = "amount")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_account_id", nullable = true)
    private Account relatedAccount;

    @Column (name = "description")
    private String description;

    

    public Transaction(TransactionType type, BigDecimal amount, Account account, Account relatedAccount, String description) {
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.relatedAccount = relatedAccount;
        this.description = description;
    }

    public Transaction() {}

    public int getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

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
    public Account getAccount(){
        return account;
    }

    public void setAccount(Account account){
        this.account = account;
    }

    public Account getRelatedAccount() {
        return relatedAccount;
    }

    public void setRelatedAccount(Account relatedAccount) {
        this.relatedAccount = relatedAccount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}