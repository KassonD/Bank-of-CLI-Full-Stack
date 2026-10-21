package com.bankofcli.backend.persistence;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

        @NotBlank 
        
        private String userName;

        @NotBlank  
        private String password;

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
