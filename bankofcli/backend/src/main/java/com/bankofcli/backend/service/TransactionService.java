package com.bankofcli.backend.service;

import org.springframework.stereotype.Service;

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

        Account relatedAccount = transaction.getRelatedAccountId() > 0
                ? accountService.getAccountById(transaction.getRelatedAccountId())
                : null;

        Transaction newTransaction = new Transaction(transaction.getType(), transaction.getAmount());
        newTransaction.setAccount(account);
        newTransaction.setRelatedAccount(relatedAccount);

        return transactionRepository.save(newTransaction);
    }
}