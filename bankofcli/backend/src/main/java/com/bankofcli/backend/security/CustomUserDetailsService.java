package com.bankofcli.backend.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.repository.AccountRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepo;

    public CustomUserDetailsService(AccountRepository accountRepo){
        this.accountRepo=accountRepo;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Account account = accountRepo.findByUserName(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

    return new org.springframework.security.core.userdetails.User(
            account.getUserName(),
            account.getPassword(),
            List.of());
}



}
