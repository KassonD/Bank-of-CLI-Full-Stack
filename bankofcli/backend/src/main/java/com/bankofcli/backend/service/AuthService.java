package com.bankofcli.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bankofcli.backend.persistence.AccountWriteDTO;
import com.bankofcli.backend.persistence.AuthResponseDTO;
import com.bankofcli.backend.repository.AccountRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountRepository accountRepo;

    public AuthService(AuthenticationManager authenticationManager, BCryptPasswordEncoder passwordEncoder,
            AccountRepository accountRepo) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.accountRepo = accountRepo;
    }

    @Transactional
    public AuthResponseDTO register(AccountWriteDTO request) {

        if (accountRepo.existsByUserName(request.getUserName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        }

    }

    public AuthResponseDTO login() {

    }

}
