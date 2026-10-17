package com.bankofcli.backend.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.domain.Transaction;
import com.bankofcli.backend.domain.TransactionType;
import com.bankofcli.backend.exceptions.AccountNotFoundException;
import com.bankofcli.backend.exceptions.InsufficientFundsException;
import com.bankofcli.backend.exceptions.InvalidAmountException;
import com.bankofcli.backend.persistence.TransactionWriteDTO;
import com.bankofcli.backend.persistence.TransferDTO;
import com.bankofcli.backend.repository.AccountRepository;
import com.bankofcli.backend.repository.TransactionRepository;

@Service
public class TransactionService {

    private static final int MONEY_SCALE = 2;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // Moves money from accountId to relatedAccountId.
    // Atomic: the whole method runs in one database transaction, so if anything
    // fails, both balance changes and the transaction row are rolled back.
 
    @Transactional
    public TransactionWriteDTO transfer(TransferDTO transferDTO) {
        BigDecimal amount = validateAmount(transferDTO.getAmount());
        int senderId = transferDTO.getAccountId();
        int recipientId = transferDTO.getRelatedAccountId();

        if (senderId == recipientId) {
            throw new InvalidAmountException("Cannot transfer money to the same account");
        }

        // Lock both rows in ascending ID order to prevent deadlocks
        Account first = lockAccount(Math.min(senderId, recipientId));
        Account second = lockAccount(Math.max(senderId, recipientId));
        Account sender = (first.getId() == senderId) ? first : second;
        Account recipient = (sender == first) ? second : first;

        BigDecimal senderBalance = balanceOf(sender);
        if (senderBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for this transfer");
        }

        sender.setBalance(senderBalance.subtract(amount));
        recipient.setBalance(balanceOf(recipient).add(amount));
        // Both accounts are managed entities, so Hibernate saves the new balances on commit.

        Transaction transaction = new Transaction(TransactionType.TRANSFER, amount);
        transaction.setAccount(sender);
        transaction.setRelatedAccount(recipient);
        transactionRepository.save(transaction);

        TransactionWriteDTO result = new TransactionWriteDTO();
        result.setType(TransactionType.TRANSFER);
        result.setAmount(amount);
        result.setAccountId(senderId);
        result.setRelatedAccountId(recipientId);
        return result;
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than 0");
        }
        if (amount.stripTrailingZeros().scale() > MONEY_SCALE) {
            throw new InvalidAmountException("Transfer amount must have at most 2 decimal places");
        }
        return amount.setScale(MONEY_SCALE);
    }

    private Account lockAccount(int accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account " + accountId + " does not exist"));
    }

    /** A new account may have a null balance, so treat null as zero. */
    private static BigDecimal balanceOf(Account account) {
        return account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
    }
}