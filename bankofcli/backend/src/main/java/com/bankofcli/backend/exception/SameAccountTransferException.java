package com.bankofcli.backend.exception;

public class SameAccountTransferException extends TransferException {

    public SameAccountTransferException() {
        super("SAME_ACCOUNT_TRANSFER", "Cannot transfer money to your own account");
    }
}
