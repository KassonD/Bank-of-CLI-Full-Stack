package com.bankofcli.backend.repository;

import com.bankofcli.backend.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByAccount_IdOrRelatedAccount_IdOrderByTimestampDesc(
            Integer accountId,
            Integer relatedAccountId
    );
}