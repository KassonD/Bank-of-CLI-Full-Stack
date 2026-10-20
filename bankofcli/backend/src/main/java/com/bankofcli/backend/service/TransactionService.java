package com.bankofcli.backend.service;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.domain.Transaction;
import com.bankofcli.backend.repository.TransactionRepository;
import com.bankofcli.backend.persistence.TransactionWriteDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Transactional
    public Transaction insertTransaction(TransactionWriteDTO transaction) {

        Account account = accountService.getAccountById(transaction.getAccountId());
        Integer relatedAccountId = transaction.getRelatedAccountId();

        Account relatedAccount = relatedAccountId != null && relatedAccountId > 0
                ? accountService.getAccountById(relatedAccountId)
                : null;

        Transaction newTransaction = new Transaction(
            transaction.getType(), 
            transaction.getAmount(), 
            account, 
            relatedAccount, 
            transaction.getDescription()
        );

        return transactionRepository.save(newTransaction);
    }
}