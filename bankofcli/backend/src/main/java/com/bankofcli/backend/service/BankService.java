package com.bankofcli.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankofcli.backend.persistence.DepositDTO;
import com.bankofcli.backend.persistence.TransferDTO;
import com.bankofcli.backend.persistence.WithdrawDTO;

@Service 
public class BankService {
    private AccountService accountService;
    private TransactionService transactionService;

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
}
