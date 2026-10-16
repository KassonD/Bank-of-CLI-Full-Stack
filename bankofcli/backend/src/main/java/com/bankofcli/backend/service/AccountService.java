package com.bankofcli.backend.service;

//import javax.security.auth.login.AccountNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;    

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.persistence.AccountWriteDTO;
import com.bankofcli.backend.repository.AccountRepository;
import com.bankofcli.backend.exceptions.AccountNotFoundException;


@Service
public class AccountService {

    private final AccountRepository accountRepo;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    @Transactional
    public Account insertAccount(AccountWriteDTO account) {

        Account newAccount = new Account(account.getFirstName(), account.getLastName(), account.getUserName(), account.getPassword());

        return accountRepo.save(newAccount);
    }

    @Transactional(readOnly = true)
    public Account getAccountByUsername(String userName) {

        Account account = accountRepo.findByUserName(userName).orElseThrow(() -> new AccountNotFoundException("Account not found with username: " + userName));
    
        return account;
    
    }

}
