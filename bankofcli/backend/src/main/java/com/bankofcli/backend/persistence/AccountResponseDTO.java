package com.bankofcli.backend.persistence;

import java.math.BigDecimal;

public class AccountResponseDTO {

    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private BigDecimal balance;

    public AccountResponseDTO (int id, String firstName, String lastName, String username, BigDecimal balance) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.balance = balance;
    }

    public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}
    	
    public String getLastName() {
		return lastName;
	}

	public String getUsername() {
		return username;
	}

	public BigDecimal getBalance() {
        return balance;
    } 
    
}
