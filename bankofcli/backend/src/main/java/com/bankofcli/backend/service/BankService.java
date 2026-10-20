package com.bankofcli.backend.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.domain.Transaction;
import com.bankofcli.backend.domain.TransactionType;
import com.bankofcli.backend.exceptions.InvalidAmountException;
import com.bankofcli.backend.persistence.DepositDTO;
import com.bankofcli.backend.persistence.TransactionResponseDTO;
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
    public TransactionResponseDTO deposit(DepositDTO depositDTO) {
        Integer accountId = depositDTO.getAccountId();
        BigDecimal amount = depositDTO.getAmount();

        TransactionWriteDTO transactionWriteDTO = new TransactionWriteDTO();
        transactionWriteDTO.setType(TransactionType.DEPOSIT);
        transactionWriteDTO.setAmount(amount);
        transactionWriteDTO.setAccountId(accountId);
        transactionWriteDTO.setRelatedAccountId(null);
        transactionWriteDTO.setDescription(depositDTO.getDescription());

        accountService.deposit(accountId, amount);
        Transaction transaction = transactionService.insertTransaction(transactionWriteDTO);
        
        return mapTransactionResponseDTO(transaction);
    }

    @Transactional
    public TransactionResponseDTO withdraw(WithdrawDTO withdrawDTO) {
        int accountId = withdrawDTO.getAccountId();
        BigDecimal amount = withdrawDTO.getAmount();

        TransactionWriteDTO transactionWriteDTO = new TransactionWriteDTO();
        transactionWriteDTO.setType(TransactionType.WITHDRAW);
        transactionWriteDTO.setAmount(amount);
        transactionWriteDTO.setAccountId(accountId);
        transactionWriteDTO.setRelatedAccountId(null);
        transactionWriteDTO.setDescription(withdrawDTO.getDescription());

        accountService.withdraw(accountId, amount);
        Transaction transaction = transactionService.insertTransaction(transactionWriteDTO);
        
        return mapTransactionResponseDTO(transaction);
    }

    @Transactional
    public TransactionResponseDTO transfer(TransferDTO transferDTO) {
        int senderId = transferDTO.getAccountId();
        int recipientId = transferDTO.getRelatedAccountId();
        BigDecimal amount = transferDTO.getAmount();

        if (senderId == recipientId) {
            throw new InvalidAmountException("Cannot transfer money to the same account");
        }

        TransactionWriteDTO transactionWriteDTO = new TransactionWriteDTO();
        transactionWriteDTO.setType(TransactionType.TRANSFER);
        transactionWriteDTO.setAmount(amount);
        transactionWriteDTO.setAccountId(senderId);
        transactionWriteDTO.setRelatedAccountId(recipientId);
        transactionWriteDTO.setDescription(transferDTO.getDescription());

        accountService.withdraw(senderId, amount);
        accountService.deposit(recipientId, amount);
        Transaction transaction = transactionService.insertTransaction(transactionWriteDTO);

        return mapTransactionResponseDTO(transaction);
    }

    private TransactionResponseDTO mapTransactionResponseDTO(Transaction transaction) {
        Account relatedAccount = transaction.getRelatedAccount();
        return new TransactionResponseDTO(
            transaction.getId(),
            transaction.getTimestamp(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getAccount().getId(),
            relatedAccount != null ? relatedAccount.getId() : null,
            transaction.getDescription()
        );
    }
}