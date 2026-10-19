package com.bankofcli.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.domain.TransactionType;
import com.bankofcli.backend.exceptions.InvalidAmountException;
import com.bankofcli.backend.persistence.DepositDTO;
import com.bankofcli.backend.persistence.TransactionWriteDTO;
import com.bankofcli.backend.persistence.TransferDTO;
import com.bankofcli.backend.persistence.WithdrawDTO;

@Service
public class BankService {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public BankService(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @Transactional
    public void deposit(DepositDTO depositDTO) {

    }

    @Transactional
    public void withdraw(WithdrawDTO withdrawDTO) {

    }

    @Transactional
    public TransactionWriteDTO transfer(TransferDTO transferDTO) {
        int senderId = transferDTO.getAccountId();
        int recipientId = transferDTO.getRelatedAccountId();

        if (senderId == recipientId) {
            throw new InvalidAmountException("Cannot transfer money to the same account");
        }

        // Lock both accounts in ascending ID order; also confirms both exist (404 otherwise)
        accountService.getAccountForUpdate(Math.min(senderId, recipientId));
        accountService.getAccountForUpdate(Math.max(senderId, recipientId));

        // The existing rules: amount > 0, sufficient funds, balance update
        Account sender = accountService.withdraw(senderId, transferDTO.getAmount());
        Account recipient = accountService.deposit(recipientId, transferDTO.getAmount());

        transactionService.createTransaction(sender, recipient, TransactionType.TRANSFER, transferDTO.getAmount());

        TransactionWriteDTO result = new TransactionWriteDTO();
        result.setType(TransactionType.TRANSFER);
        result.setAmount(transferDTO.getAmount());
        result.setAccountId(senderId);
        result.setRelatedAccountId(recipientId);
        return result;
    }
}