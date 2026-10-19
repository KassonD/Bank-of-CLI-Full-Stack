package com.bankofcli.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // Lock both accounts in ascending ID order to prevent deadlocks
        accountService.getAccountForUpdate(Math.min(senderId, recipientId));
        accountService.getAccountForUpdate(Math.max(senderId, recipientId));

        accountService.withdraw(senderId, transferDTO.getAmount());
        accountService.deposit(recipientId, transferDTO.getAmount());

        TransactionWriteDTO transaction = new TransactionWriteDTO();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(transferDTO.getAmount());
        transaction.setAccountId(senderId);
        transaction.setRelatedAccountId(recipientId);

        transactionService.insertTransaction(transaction);

        return transaction;
    }
}