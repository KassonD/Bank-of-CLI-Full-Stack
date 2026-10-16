package com.bankofcli.backend.repository;

import com.bankofcli.backend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface AccountRepository
        extends JpaRepository<Account, Integer> {

    Optional<Account> findByUserName(String userName);

    boolean existsByUserName(String userName);

////////////////////
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") int id);
////////////////////
}
