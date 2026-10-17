package com.bankofcli.backend.exception;

/**
 * Base type for every business-rule failure in a transfer.
 * It carries no HTTP details: the service layer stays independent of the web
 * layer, and TransferExceptionHandler maps each subtype to a status code.
 */
public abstract class TransferException extends RuntimeException {

    private final String code;

    protected TransferException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
