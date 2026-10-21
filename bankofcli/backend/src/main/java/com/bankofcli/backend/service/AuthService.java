package com.bankofcli.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.exceptions.AccountNotFoundException;
import com.bankofcli.backend.persistence.AccountWriteDTO;
import com.bankofcli.backend.persistence.AuthResponseDTO;
import com.bankofcli.backend.persistence.LoginRequestDTO;
import com.bankofcli.backend.repository.AccountRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountRepository accountRepo;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

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

        Account account = new Account(
            request.getFirstName(),
            request.getLastName(),
            request.getUserName(),
            passwordEncoder.encode(request.getPassword()));

        Account saved = accountRepo.save(account);

        return AuthResponseDTO.from(saved);

    }

    public AuthResponseDTO login(
            LoginRequestDTO request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUserName(), request.getPassword()));

        if (servletRequest.getSession(false) != null) {
            servletRequest.changeSessionId();
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        return toResponse(authentication);
    }

    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        logoutHandler.logout(request, response, authentication);
    }
    

    @Transactional(readOnly = true)
    public AuthResponseDTO toResponse(Authentication authentication) {
        Account account = accountRepo.findByUserName(authentication.getName())
                .orElseThrow(() -> new AccountNotFoundException(
                        "No account found for user " + authentication.getName()));
        return AuthResponseDTO.from(account);
    }

}
