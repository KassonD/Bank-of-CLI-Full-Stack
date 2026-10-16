package com.bankofcli.backend.rest;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.service.AccountService;
import com.bankofcli.backend.persistence.AccountWriteDTO;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bankofcli.backend.service.AccountService;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(params = "userName")
    public Account getAccountByUserNameAccount(@RequestParam String userName) {
        return accountService.getAccountByUsername(userName);
    }
    

    @PostMapping
    public ResponseEntity<Account> insertAccount(@RequestBody AccountWriteDTO account) {
        
        Account savedAccount = accountService.insertAccount(account);

                return ResponseEntity.created(ServletUriComponentsBuilder
                                                .fromCurrentRequest()
                                                .path("/{id}")
                                                .buildAndExpand(savedAccount.getId())
                                                .toUri()).body(savedAccount);
    }
    

}
