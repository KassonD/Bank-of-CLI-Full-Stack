package com.bankofcli.backend.repository;

import com.bankofcli.backend.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<Transaction, Integer> {

}
