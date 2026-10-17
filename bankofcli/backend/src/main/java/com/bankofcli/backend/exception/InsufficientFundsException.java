package com.bankofcli.backend.exception;

public class InsufficientFundsException extends TransferException {

    public InsufficientFundsException() {
        // The balance is deliberately left out of the message so it never leaks into logs.
        super("INSUFFICIENT_FUNDS", "Insufficient funds for this transfer");
    }
}
