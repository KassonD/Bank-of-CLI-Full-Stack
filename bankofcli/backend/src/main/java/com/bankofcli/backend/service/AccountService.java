package com.bankofcli.backend.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.exceptions.AccountNotFoundException;
import com.bankofcli.backend.exceptions.InsufficientFundsException;
import com.bankofcli.backend.exceptions.InvalidAmountException;
import com.bankofcli.backend.repository.AccountRepository;


@Service
public class AccountService {

    private final AccountRepository accountRepository;


    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public Account getAccountById(int id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("No account found with id " + id));
    }

    private void validateAmount(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("Amount must be greater than zero, was " + amount);
        }
    }

    private BigDecimal balanceOf(Account account){
        return account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(int id){
        return balanceOf(getAccountById(id));
    }

    @Transactional
    public Account deposit(int id, BigDecimal amount){
        validateAmount(amount);

        Account account = getAccountById(id);
        account.setBalance(balanceOf(account).add(amount));

        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(int id, BigDecimal amount){
        validateAmount(amount);

        Account account = getAccountById(id);
        BigDecimal balance = balanceOf(account);

        if (balance.compareTo(amount) < 0){
            throw new InsufficientFundsException(
                    "Insufficient funds: balance is " + balance + ", requested " + amount);
        }

        account.setBalance(balance.subtract(amount));

        return accountRepository.save(account);
    }

}
