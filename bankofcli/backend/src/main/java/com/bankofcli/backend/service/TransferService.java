package com.bankofcli.backend.service;

import com.bankofcli.backend.dto.TransferRequest;
import com.bankofcli.backend.dto.TransferResponse;

/**
 * Moves money between two accounts.
 * The controller depends on this interface not on the implementation, so the
 * implementation can be swapped or mocked without touching the web layer.
 */
public interface TransferService {

    /**
     * Transfers {@code request.amount()} from the account owned by
     * {@code senderUserName} to {@code request.toAccountId()}.
     * Either both balances change and one transaction row is written, or nothing changes.
     *
     * @param senderUserName the authenticated user, taken from the JWT
     * @throws com.bankofcli.backend.exception.TransferException when a business rule fails
     */
    TransferResponse transfer(String senderUserName, TransferRequest request);
}
