package com.bankofcli.backend.persistence;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AccountWriteDTO {

	@NotBlank 
    private String firstName;

	@NotBlank
    private String lastName;

	@NotBlank
	@Size(min = 3, max = 50) 
    private String username;

	@NotBlank
	@Size(min = 8, max = 100) 
    private String password;

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    
}
