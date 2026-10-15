package com.bankofcli.backend.repository;

import com.bankofcli.backend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository
        extends JpaRepository<Account, Integer> {

    Optional<Account> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
