package com.bankofcli.backend.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.repository.AccountRepository;

/**
 * CustomUserDetailsManager
 */
@Service
public class CustomUserDetailsManager implements UserDetailsManager {

    private final AccountRepository accountRepo;
    private BCryptPasswordEncoder passwordEncoder;

    public CustomUserDetailsManager(AccountRepository accountRepo, BCryptPasswordEncoder passwordEncoder){
        this.accountRepo=accountRepo;
        this.passwordEncoder=passwordEncoder;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
    }

    @Override
    public void changePassword(@Nullable String arg0, @Nullable String arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public void createUser(UserDetails user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        String username = user.getUsername();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        // make sure this is enforced at db level, we don't want race conditions
        if (userExists(username)) {
            throw new IllegalArgumentException("User already exists: " + username);
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        Account newUser = new Account();
        newUser.setUserName(username);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        accountRepo.save(newUser);
    }

    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public void updateUser(UserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public boolean userExists(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'userExists'");
    }

}
