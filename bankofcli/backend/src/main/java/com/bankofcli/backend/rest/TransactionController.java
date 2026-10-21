package com.bankofcli.backend.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankofcli.backend.persistence.DepositDTO;
import com.bankofcli.backend.persistence.TransactionResponseDTO;
import com.bankofcli.backend.persistence.TransferDTO;
import com.bankofcli.backend.persistence.WithdrawDTO;
import com.bankofcli.backend.service.BankService;
import com.bankofcli.backend.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final BankService bankService;
    private final TransactionService transactionService;

    public TransactionController(BankService bankService, TransactionService transactionService) {
        this.bankService = bankService;
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@RequestBody DepositDTO depositDTO) {
        TransactionResponseDTO result = bankService.deposit(depositDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@RequestBody WithdrawDTO withdrawDTO) {
        TransactionResponseDTO result = bankService.withdraw(withdrawDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@RequestBody TransferDTO transferDTO) {
        TransactionResponseDTO result = bankService.transfer(transferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @GetMapping("/history/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(@PathVariable Integer accountId) {

        List<TransactionResponseDTO> transactions = transactionService.getTransactionsForAccount(accountId);

        return ResponseEntity.ok(transactions);
    }
    
}