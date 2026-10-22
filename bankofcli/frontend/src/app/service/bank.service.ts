import { Injectable, InjectionToken, computed, inject, signal } from '@angular/core';
import { Observable, defer, finalize, forkJoin, map, switchMap, tap } from 'rxjs';
import { Account } from '../contracts/account';
import { Transaction } from '../contracts/transaction';
import { assertSufficientFunds, assertValidAmount, BankError } from '../errors/bank.errors';
import { HttpBankDataSource } from '../data_source/http-bank-data-source';

// An account that exists in the system always has an id and a balance.
export type StoredAccount = Account & { id: number; balance: number };

// What the UI sees: an existing account, without the password.
export type AccountSummary = Omit<StoredAccount, 'password'>;

export interface TransactionResult {
  account: AccountSummary;   // account state AFTER the transaction
  transaction: Transaction;  // the newly recorded transaction
}

export interface BankDataSource {
  getAccount(id: number): Observable<AccountSummary>;
  getTransactions(accountId: number): Observable<Transaction[]>;
  deposit(accountId: number, amount: number, description?: string): Observable<Transaction>;
  withdraw(accountId: number, amount: number, description?: string): Observable<Transaction>;
  transfer(fromId: number, toId: number, amount: number, description?: string): Observable<Transaction>;
}

@Injectable({ providedIn: 'root' })
export class BankService {
  private readonly api = inject(HttpBankDataSource);

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

  // Reads another account without replacing the loaded one. Used for transfer names.
  lookupAccount(id: number): Observable<AccountSummary> {
    return this.api.getAccount(id);
  }

  // Replaces the stored history with a fresh list for the loaded account.
  refreshTransactions(): Observable<Transaction[]> {
    return this.track(
      defer(() => this.api.getTransactions(this.requireAccountId())).pipe(
        tap((transactions) => this._transactions.set(transactions)),
      ),
    );
  }

  // Checks that the amount is valid, then posts a deposit and refreshes the account.
  deposit(amount: number, description?: string): Observable<Transaction> {
    return this.runTransaction(() => {
      assertValidAmount(amount);
      return this.api.deposit(this.requireAccountId(), amount,description); 
    });
  }

  // Checks the amount and the current balance, then posts a withdrawal and refreshes the account.
  withdraw(amount: number, description?: string): Observable<Transaction> {
    return this.runTransaction(() => {
      assertValidAmount(amount);
      assertSufficientFunds(this.balance(), amount);
      return this.api.withdraw(this.requireAccountId(), amount,description);
    });
  }

  // Checks the recipient, blocks a transfer to the same account, then posts the transfer and refreshes the account.
  transfer(toAccountId: number, amount: number, description?: string): Observable<Transaction> {
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

  // Runs a money request, reloads the account, and puts the new transaction at the front of the history.
  private runTransaction(request: () => Observable<Transaction>): Observable<Transaction> {
    return this.track(
      defer(request).pipe(
        switchMap((transaction) =>
          this.api.getAccount(this.requireAccountId()).pipe(
            tap((account) => {
              this._account.set(account);
              this._transactions.update((list) => [transaction, ...list]);
            }),
            map(() => transaction),
          )
        )
      )
    );
  }
  
  // Returns the loaded account id. Throws if loadAccount has not stored an account yet.
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
