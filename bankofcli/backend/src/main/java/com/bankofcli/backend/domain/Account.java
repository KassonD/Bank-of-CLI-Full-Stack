package com.bankofcli.backend.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.ArrayList;

@Entity 
@Table(name = "account")
public class Account {

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions = new ArrayList<>();

    public List<Transaction> getTransactions(){
        return transactions;
    }

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "first_name")
    private String firstName;

    @Column (name = "last_name")
    private String lastName;

    //commonly used constraints in financial applications. 
    // max 17 digis + 2 decimal places.
    // zero by default
    @Column (name = "balance", nullable=false, precision = 19, scale = 2)
    private BigDecimal balance =  BigDecimal.ZERO;

    //username must be unique
    @Column (name = "user_name", unique = true, nullable = false)
    private String userName;

    //password cant be nullable, length is 100 to fix Bcrypt hash
    @Column (name = "password", nullable = false, length = 100)
    private String password;

    public Account() {}

    public Account(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    
}
