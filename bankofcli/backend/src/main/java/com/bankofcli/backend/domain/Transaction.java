package com.bankofcli.backend.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "timestamp")
    private LocalDate timestamp;

    @Column (name = "type")
    private String type;

    @Column (name = "amount")
    private BigDecimal amount;

    public Transaction(LocalDate timestamp, String type, BigDecimal amount) {
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
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
