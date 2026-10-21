package com.bankofcli.backend.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AccountWriteDTO {

	@NotBlank 
    private String firstName;

	@NotBlank
    private String lastName;

	@NotBlank
	@Size(min = 3, max = 50) 
	@JsonProperty("username")
    private String userName;

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
