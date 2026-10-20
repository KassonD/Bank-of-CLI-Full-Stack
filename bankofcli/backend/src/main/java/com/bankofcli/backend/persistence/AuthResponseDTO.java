package com.bankofcli.backend.persistence;

import java.math.BigDecimal;

import com.bankofcli.backend.domain.Account;

public class AuthResponseDTO {

    private final String userName;
    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final BigDecimal balance;

    public String getUserName() {
        return userName;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AuthResponseDTO(String userName, Integer id, String firstName, String lastName, BigDecimal balance) {
        this.userName = userName;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

    public static AuthResponseDTO from(Account account) {
        return new AuthResponseDTO(
            account.getUserName(),
            account.getId(),
            account.getFirstName(),
            account.getLastName(),
            account.getBalance());
    }

    

}