package com.bankofcli.backend.persistence;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

        @NotBlank 
        
        private String username;

        @NotBlank  
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUserName(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

}
