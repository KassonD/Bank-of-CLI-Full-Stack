package com.bankofcli.backend.rest;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.service.AccountService;
import com.bankofcli.backend.persistence.AccountResponseDTO;
import com.bankofcli.backend.persistence.AccountWriteDTO;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    // this basically converts the Account entity retrieved from the service into a DTO that the frontend can use to display the data it needs
    private AccountResponseDTO toResponseDTO(Account account) { 
        return new AccountResponseDTO(account.getId(), account.getFirstName(), account.getLastName(), account.getUserName(), account.getBalance()); // this is the data that will be displayed on the account overview
    }

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(params = "userName")
    public AccountResponseDTO getAccountByUserNameAccount(
        @RequestParam("userName") String userName) {

        Account account = accountService.getAccountByUsername(userName);
        return toResponseDTO(account);
    }

    @GetMapping("/{id}")
    public AccountResponseDTO getAccountById(@PathVariable int id) {
            
        Account account = accountService.getAccountById(id);

        return toResponseDTO(account); // sends the DTO containing firName, username, and Balance to the frontend   
    }
    

    @PostMapping
    public ResponseEntity<AccountResponseDTO> insertAccount(@RequestBody AccountWriteDTO account) {
        
        Account savedAccount = accountService.insertAccount(account); // this converts AccountWriteDTO to an Account entity
        // then in order to not return things such as the password, we convert it into a ResponseDTO
        AccountResponseDTO response = toResponseDTO(savedAccount);

                return ResponseEntity.created(ServletUriComponentsBuilder
                                                .fromCurrentRequest()
                                                .path("/{id}")
                                                .buildAndExpand(savedAccount.getId())
                                                .toUri()).body(response);
    }
    

}
