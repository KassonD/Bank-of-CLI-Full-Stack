package com.bankofcli.backend.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.domain.Transaction;
import com.bankofcli.backend.domain.TransactionType;
import com.bankofcli.backend.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Account account, Account relatedAccount,
                                         TransactionType type, BigDecimal amount) {
        Transaction transaction = new Transaction(type, amount);
        transaction.setAccount(account);
        transaction.setRelatedAccount(relatedAccount);
        return transactionRepository.save(transaction);
    }
}