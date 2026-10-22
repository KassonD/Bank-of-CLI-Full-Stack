import { HttpClient } from "@angular/common/http";
import { AccountSummary, BankDataSource, TransactionResult } from "../service/bank.service";
import { Observable } from "rxjs";
import { Transaction } from "../contracts/transaction";
import { inject } from "@angular/core";

//Implementation of BankDataSource - Defines the HTTP Calls we make to the backend
export class HttpBankDataSource implements BankDataSource {

    private readonly http = inject(HttpClient);
    apiUrl: string = '/api';

    // GET */api/accounts/{id}
    getAccount(id: number): Observable<AccountSummary> {
        return this.http.get<AccountSummary>(`${this.apiUrl}/accounts/${id}`);
    }
    // GET */api/transactions/history/{id}
    getTransactions(accountId: number): Observable<Transaction[]> {
        return this.http.get<Transaction[]>(`${this.apiUrl}/transactions/history/${accountId}`);
    }
    // POST */api/transactions/deposit
    deposit(accountId: number, amount: number, description?: string): Observable<Transaction> {
        return this.http.post<Transaction>(`${this.apiUrl}/transactions/deposit`, { accountId, amount, description });
    }
    // POST */api/transactions/withdraw
    withdraw(accountId: number, amount: number, description?: string): Observable<Transaction> {
        return this.http.post<Transaction>(`${this.apiUrl}/transactions/withdraw`, { accountId, amount, description });
    }
    // POST */api/transactions/transfer
    transfer(accountId: number, relatedAccountId: number, amount: number, description?: string): Observable<Transaction> {
        return this.http.post<Transaction>(`${this.apiUrl}/transactions/transfer`, { accountId, relatedAccountId, amount, description });
    }
    
    
}