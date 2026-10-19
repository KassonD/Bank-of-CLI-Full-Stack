package com.bankofcli.backend.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankofcli.backend.persistence.TransactionWriteDTO;
import com.bankofcli.backend.persistence.TransferDTO;
import com.bankofcli.backend.service.BankService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final BankService bankService;

    public TransactionController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionWriteDTO> transfer(@RequestBody TransferDTO transferDTO) {
        TransactionWriteDTO result = bankService.transfer(transferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}