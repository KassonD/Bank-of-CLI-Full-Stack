package com.bankofcli.backend.exception;

/**
 * The controller's bean validation already rejects bad amounts. The service
 * checks again because it may also be called from code that skips the
 * controller, and the rule belongs to the domain rather than the HTTP layer.
 */
public class InvalidTransferAmountException extends TransferException {

    public InvalidTransferAmountException(String message) {
        super("INVALID_AMOUNT", message);
    }
}
