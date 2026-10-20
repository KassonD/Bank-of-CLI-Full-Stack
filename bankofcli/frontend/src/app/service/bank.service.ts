import { Injectable, InjectionToken, computed, inject, signal } from '@angular/core';
import { Observable, defer, finalize, forkJoin, map, tap, timer } from 'rxjs';
import { Account } from '../contracts/account';
import { Transaction } from '../contracts/transaction';
import { getAccounts, getTransactionsByAccountId } from '../mock/api';

// An account that exists in the system always has an id and a balance.
type StoredAccount = Account & { id: number; balance: number };

// What the UI sees: an existing account, without the password.
export type AccountSummary = Omit<StoredAccount, 'password'>;

export interface TransactionResult {
  account: AccountSummary;   // account state AFTER the transaction
  transaction: Transaction;  // the newly recorded transaction
}

export type BankErrorCode =
  | 'INVALID_AMOUNT'
  | 'INSUFFICIENT_FUNDS'
  | 'ACCOUNT_NOT_FOUND'
  | 'SAME_ACCOUNT'
  | 'NO_ACTIVE_ACCOUNT';

export class BankError extends Error {
  constructor(public readonly code: BankErrorCode, message: string) {
    super(message);
    this.name = 'BankError';
  }
}

export interface BankDataSource {
  getAccount(id: number): Observable<AccountSummary>;
  getTransactions(accountId: number): Observable<Transaction[]>;
  deposit(accountId: number, amount: number, description?: string): Observable<TransactionResult>;
  withdraw(accountId: number, amount: number, description?: string): Observable<TransactionResult>;
  transfer(fromId: number, toId: number, amount: number, description?: string): Observable<TransactionResult>;
}

export const BANK_DATA_SOURCE = new InjectionToken<BankDataSource>('BankDataSource', {
  providedIn: 'root',
  factory: () => new MockBankDataSource(),
});

function roundMoney(value: number): number {
  return Math.round(value * 100) / 100;
}

function assertValidAmount(amount: number): void {
  if (typeof amount !== 'number' || !Number.isFinite(amount) || amount <= 0) {
    throw new BankError('INVALID_AMOUNT', 'Amount must be a number greater than 0.');
  }
  const cents = amount * 100;
  if (Math.abs(cents - Math.round(cents)) > 1e-9) {
    throw new BankError('INVALID_AMOUNT', 'Amount cannot have more than 2 decimal places.');
  }
}

function assertSufficientFunds(balance: number, amount: number): void {
  if (amount > balance) {
    throw new BankError('INSUFFICIENT_FUNDS', 'Insufficient funds for this transaction.');
  }
}

function normalizeTimestamp(value: string | undefined): string {
  if (!value) return '';
  let iso = value.trim().replace(' ', 'T');
  if (iso.includes('T')) {
    iso = iso
      .replace(/([+-]\d{2})$/, '$1:00')          // -07   -> -07:00
      .replace(/([+-]\d{2})(\d{2})$/, '$1:$2');  // -0700 -> -07:00
  }
  return Number.isNaN(Date.parse(iso)) ? value : iso;
}

function toSummary(account: StoredAccount): AccountSummary {
  const { password: _password, ...summary } = account;
  return summary;
}

const MOCK_LATENCY_MS = 600;

export class MockBankDataSource implements BankDataSource {
  // Seed accounts: skip any without an id, default a missing balance to 0.
  private readonly accounts: StoredAccount[] = getAccounts().flatMap((a) =>
    a.id === undefined ? [] : [{ ...a, id: a.id, balance: a.balance ?? 0 }],
  );

  // Seed transactions: api.ts only offers a per-account lookup, so collect
  // each account's transactions into one list (needed for incoming transfers
  // and for generating new ids).
  private readonly transactions: Transaction[] = this.accounts
    .flatMap((a) => getTransactionsByAccountId(a.id))
    .map((t) => ({ ...t, timestamp: normalizeTimestamp(t.timestamp) }));

  constructor(private readonly latencyMs: number = MOCK_LATENCY_MS) {}

  getAccount(id: number): Observable<AccountSummary> {
    return this.respond(() => toSummary(this.findAccount(id)));
  }

  getTransactions(accountId: number): Observable<Transaction[]> {
    return this.respond(() => {
      this.findAccount(accountId);
      return this.transactions
        .filter((t) => t.accountId === accountId || t.relatedAccountId === accountId)
        .sort((a, b) => b.id - a.id) // newest first
        .map((t) => ({ ...t, timestamp: normalizeTimestamp(t.timestamp) }));
    });
  }

  deposit(accountId: number, amount: number, description?: string): Observable<TransactionResult> {
    return this.respond(() => {
      assertValidAmount(amount);
      const account = this.findAccount(accountId);
      account.balance = roundMoney(account.balance + amount);
      return { account: toSummary(account), transaction: this.record('deposit', amount, accountId, description) };
    });
  }

  withdraw(accountId: number, amount: number, description?: string): Observable<TransactionResult> {
    return this.respond(() => {
      assertValidAmount(amount);
      const account = this.findAccount(accountId);
      assertSufficientFunds(account.balance, amount);
      account.balance = roundMoney(account.balance - amount);
      return { account: toSummary(account), transaction: this.record('withdraw', amount, accountId, description) };
    });
  }

  transfer(fromId: number, toId: number, amount: number, description?: string): Observable<TransactionResult> {
    return this.respond(() => {
      assertValidAmount(amount);
      if (fromId === toId) {
        throw new BankError('SAME_ACCOUNT', 'You cannot transfer money to your own account.');
      }
      const from = this.findAccount(fromId);
      const to = this.findAccount(toId);
      assertSufficientFunds(from.balance, amount);
      from.balance = roundMoney(from.balance - amount);
      to.balance = roundMoney(to.balance + amount);
      return { account: toSummary(from), transaction: this.record('transfer', amount, fromId, description,toId) };
    });
  }

  // Runs `work` after the simulated delay. Thrown errors become Observable errors.
  private respond<T>(work: () => T): Observable<T> {
    return timer(this.latencyMs).pipe(map(() => work()));
  }

  private findAccount(id: number): StoredAccount {
    const account = this.accounts.find((a) => a.id === id);
    if (!account) {
      throw new BankError('ACCOUNT_NOT_FOUND', `Account #${id} was not found.`);
    }
    return account;
  }

  private record(
    type: string,
    amount: number,
    accountId: number,
    description?: string,
    relatedAccountId?: number,
  ): Transaction {
    const text = description?.trim();
    const transaction: Transaction = {
      id: this.transactions.reduce((max, t) => Math.max(max, t.id), 0) + 1,
      accountId,
      type,
      amount,
      timestamp: new Date().toISOString(),
      ...(relatedAccountId !== undefined && { relatedAccountId }),
      ...(text && { description: text ?? '', }),
    };
    this.transactions.push(transaction);
    return { ...transaction };
  }
}

@Injectable({ providedIn: 'root' })
export class BankService {
  private readonly api = inject(BANK_DATA_SOURCE);

  private readonly _account = signal<AccountSummary | null>(null);
  private readonly _transactions = signal<Transaction[]>([]);
  private readonly _pending = signal(0);

  // The logged-in user's account, or null before loadAccount() / after clear().
  readonly account = this._account.asReadonly();
  // History for the active account, newest first. Includes incoming transfers.
  readonly transactions = this._transactions.asReadonly();
  readonly balance = computed(() => this._account()?.balance ?? 0);
  ////////////////////////////////////////////////////////////////
  readonly firstName = computed(() => this._account()?.firstName ?? '');
  readonly lastUpdated = computed(() => {const transactions = this._transactions();
    return transactions[0]?.timestamp ?? '';
  });
  ////////////////////////////////////////////////////////////////
  readonly hasTransactions = computed(() => this._transactions().length > 0);
  // True while any request made through this service is in flight (for spinners).
  readonly isLoading = computed(() => this._pending() > 0);

  // Call once after a successful login. Loads the account and its history.
  loadAccount(accountId: number): Observable<AccountSummary> {
    return this.track(
      forkJoin({
        account: this.api.getAccount(accountId),
        transactions: this.api.getTransactions(accountId),
      }).pipe(
        tap(({ account, transactions }) => {
          this._account.set(account);
          this._transactions.set(transactions);
        }),
        map(({ account }) => account),
      ),
    );
  }

  refreshTransactions(): Observable<Transaction[]> {
    return this.track(
      defer(() => this.api.getTransactions(this.requireAccountId())).pipe(
        tap((transactions) => this._transactions.set(transactions)),
      ),
    );
  }

  deposit(amount: number, description?: string): Observable<TransactionResult> {
    return this.runTransaction(() => {
      assertValidAmount(amount);
      return this.api.deposit(this.requireAccountId(), amount,description); 
      //aaa
    });
  }

  withdraw(amount: number, description?: string): Observable<TransactionResult> {
    return this.runTransaction(() => {
      assertValidAmount(amount);
      assertSufficientFunds(this.balance(), amount);
      return this.api.withdraw(this.requireAccountId(), amount,description);
    });
  }

  transfer(toAccountId: number, amount: number, description?: string): Observable<TransactionResult> {
    return this.runTransaction(() => {
      assertValidAmount(amount);
      if (!Number.isInteger(toAccountId) || toAccountId <= 0) {
        throw new BankError('ACCOUNT_NOT_FOUND', 'Please enter a valid recipient account ID.');
      }
      const fromId = this.requireAccountId();
      if (toAccountId === fromId) {
        throw new BankError('SAME_ACCOUNT', 'You cannot transfer money to your own account.');
      }
      assertSufficientFunds(this.balance(), amount);
      return this.api.transfer(fromId, toAccountId, amount,description);
    });
  }

  // Helper for the history table: 'in' means money came into the active
  // account (deposit or incoming transfer), 'out' means it left.
  getDirection(transaction: Transaction): 'in' | 'out' {
    if (transaction.type === 'deposit') return 'in';
    if (transaction.type === 'transfer') {
      return transaction.accountId === this._account()?.id ? 'out' : 'in';
    }
    return 'out';
  }

  // Call on logout.
  clear(): void {
    this._account.set(null);
    this._transactions.set([]);
  }

  private runTransaction(request: () => Observable<TransactionResult>): Observable<TransactionResult> {
    return this.track(
      defer(request).pipe(
        tap(({ account, transaction }) => {
          this._account.set(account);
          this._transactions.update((list) => [transaction, ...list]);
        }),
      ),
    );
  }

  private requireAccountId(): number {
    const account = this._account();
    if (!account) {
      throw new BankError('NO_ACTIVE_ACCOUNT', 'No account is loaded. Please log in again.');
    }
    return account.id;
  }

  // Counts in-flight requests so `isLoading` stays correct with parallel calls.
  private track<T>(source: Observable<T>): Observable<T> {
    return defer(() => {
      this._pending.update((n) => n + 1);
      return source.pipe(finalize(() => this._pending.update((n) => n - 1)));
    });
  }
}
