package com.bankofcli.backend.service;

import java.math.BigDecimal;

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
    public TransactionWriteDTO deposit(DepositDTO depositDTO) {
        int accountId = depositDTO.getAccountId();
        BigDecimal amount = depositDTO.getAmount();

        TransactionWriteDTO transaction = new TransactionWriteDTO();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setAccountId(accountId);
        transaction.setRelatedAccountId(-1);

        accountService.deposit(accountId, amount);
        transactionService.insertTransaction(transaction);
        
        return transaction;
    }

    @Transactional
    public TransactionWriteDTO withdraw(WithdrawDTO withdrawDTO) {
        int accountId = withdrawDTO.getAccountId();
        BigDecimal amount = withdrawDTO.getAmount();

        TransactionWriteDTO transaction = new TransactionWriteDTO();
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(amount);
        transaction.setAccountId(accountId);
        transaction.setRelatedAccountId(-1);

        accountService.deposit(accountId, amount);
        transactionService.insertTransaction(transaction);
        
        return transaction;
    }

    @Transactional
    public TransactionWriteDTO transfer(TransferDTO transferDTO) {
        int senderId = transferDTO.getAccountId();
        int recipientId = transferDTO.getRelatedAccountId();

        if (senderId == recipientId) {
            throw new InvalidAmountException("Cannot transfer money to the same account");
        }

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