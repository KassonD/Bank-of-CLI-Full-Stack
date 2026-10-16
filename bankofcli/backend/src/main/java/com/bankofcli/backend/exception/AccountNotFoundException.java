package com.bankofcli.backend.exception;

public class AccountNotFoundException extends TransferException {

    public AccountNotFoundException(int accountId) {
        super("ACCOUNT_NOT_FOUND", "Account " + accountId + " does not exist");
    }

    public AccountNotFoundException(String userName) {
        super("ACCOUNT_NOT_FOUND", "No account found for user '" + userName + "'");
    }
}
