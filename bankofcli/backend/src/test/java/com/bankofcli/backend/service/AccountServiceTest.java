package com.bankofcli.backend.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.exceptions.AccountNotFoundException;
import com.bankofcli.backend.exceptions.InsufficientFundsException;
import com.bankofcli.backend.exceptions.InvalidAmountException;
import com.bankofcli.backend.repository.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account accountWithBalance(String balance) {
        Account account = new Account("Test", "User", "testuser", "password");
        account.setBalance(new BigDecimal(balance));
        return account;
    }

    @Test
    void depositAddsToBalance() {
        Account account = accountWithBalance("100");
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        accountService.deposit(1, new BigDecimal("50"));

        assertEquals(0, new BigDecimal("150").compareTo(account.getBalance()));
        verify(accountRepository).save(account);
    }

    @Test
    void withdrawSubtractsFromBalance() {
        Account account = accountWithBalance("100");
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        accountService.withdraw(1, new BigDecimal("30"));

        assertEquals(0, new BigDecimal("70").compareTo(account.getBalance()));
        verify(accountRepository).save(account);
    }

    @Test
    void withdrawFailsAndChangesNothingWhenBalanceIsTooLow() {
        Account account = accountWithBalance("100");
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.withdraw(1, new BigDecimal("500")));

        assertEquals(0, new BigDecimal("100").compareTo(account.getBalance()));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void depositThrowsOnZeroAmount() {
        assertThrows(InvalidAmountException.class,
                () -> accountService.deposit(1, BigDecimal.ZERO));

        verifyNoInteractions(accountRepository);
    }

    @Test
    void getAccountByIdThrowsWhenNotFound() {
        when(accountRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccountById(99));
    }
}
