package com.bankofcli.backend.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankofcli.backend.dto.TransferRequest;
import com.bankofcli.backend.dto.TransferResponse;
import com.bankofcli.backend.service.TransferService;

import jakarta.validation.Valid;

/**
 * POST /api/transfers
 *
 * This class  validates input, resolves who the caller
 * is, and delegates. Every banking rule lives in TransferService.
 */
@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request,
                                                     Principal principal) {
        TransferResponse response = transferService.transfer(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
