package com.bankofcli.backend.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.domain.Transaction;
import com.bankofcli.backend.dto.TransferRequest;
import com.bankofcli.backend.dto.TransferResponse;
import com.bankofcli.backend.exception.AccountNotFoundException;
import com.bankofcli.backend.exception.InsufficientFundsException;
import com.bankofcli.backend.exception.InvalidTransferAmountException;
import com.bankofcli.backend.exception.SameAccountTransferException;
import com.bankofcli.backend.repository.AccountRepository;
import com.bankofcli.backend.repository.TransactionRepository;

@Service
public class TransferServiceImpl implements TransferService {

    static final String TRANSFER_TYPE = "transfer";
    private static final int MONEY_SCALE = 2;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Clock clock;

    public TransferServiceImpl(AccountRepository accountRepository,
                               TransactionRepository transactionRepository,
                               Clock clock) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public TransferResponse transfer(String senderUserName, TransferRequest request) {
        BigDecimal amount = validateAmount(request.amount());
        int recipientId = request.toAccountId();

        // Look up the sender's ID without a lock. The row is locked below,
        // together with the recipient, in a fixed order.
        int senderId = accountRepository.findByUserName(senderUserName)
                .orElseThrow(() -> new AccountNotFoundException(senderUserName))
                .getId();

        if (senderId == recipientId) {
            throw new SameAccountTransferException();
        }

        LockedPair locked = lockInIdOrder(senderId, recipientId);
        Account sender = locked.byId(senderId);
        Account recipient = locked.byId(recipientId);

        BigDecimal senderBalance = balanceOf(sender);
        if (senderBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        BigDecimal newSenderBalance = senderBalance.subtract(amount);
        sender.setBalance(newSenderBalance);
        recipient.setBalance(balanceOf(recipient).add(amount));
        // The accounts are managed entities, so Hibernate flushes the new
        // balances on commit. No explicit save() is needed.

        Transaction record = new Transaction(OffsetDateTime.now(clock), TRANSFER_TYPE, amount);
        record.setAccount(sender);
        record.setRelatedAccount(recipient);
        Transaction saved = transactionRepository.save(record);

        return new TransferResponse(
                saved.getId(),
                saved.getTimestamp(),
                saved.getType(),
                saved.getAmount(),
                senderId,
                recipientId,
                newSenderBalance);
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidTransferAmountException("Transfer amount must be greater than 0");
        }
        // stripTrailingZeros lets 10.50 and 10.500 through, but rejects 10.505.
        if (amount.stripTrailingZeros().scale() > MONEY_SCALE) {
            throw new InvalidTransferAmountException("Transfer amount must have at most 2 decimal places");
        }
        return amount.setScale(MONEY_SCALE);
    }

    private LockedPair lockInIdOrder(int idA, int idB) {
        int first = Math.min(idA, idB);
        int second = Math.max(idA, idB);
        Account firstLocked = lockOrThrow(first);
        Account secondLocked = lockOrThrow(second);
        return new LockedPair(firstLocked, secondLocked);
    }

    private Account lockOrThrow(int accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    /** Account.balance has no default yet, so a new account may hold null. */
    private static BigDecimal balanceOf(Account account) {
        return account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
    }

    private record LockedPair(Account first, Account second) {
        Account byId(int id) {
            return first.getId() == id ? first : second;
        }
    }
}
