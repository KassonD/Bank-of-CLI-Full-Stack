import { AfterViewInit, Component, effect, inject, signal, untracked, ViewChild } from '@angular/core';

import { CurrencyPipe } from '@angular/common';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';

import { MatSort, MatSortModule } from '@angular/material/sort';

import { MatFormFieldModule } from '@angular/material/form-field';

import { MatInputModule } from '@angular/material/input';

import { Transaction } from '../../contracts/transaction';
import { BankService } from '../../service/bank.service';
import { User } from '../../service/user';

@Component({
  selector: 'app-history',

  imports: [
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    CurrencyPipe,
  ],

  templateUrl: './history.html',
  styleUrl: './history.css',
})
export class History implements AfterViewInit {
  
  private readonly bankService = inject(BankService);
  private readonly userService = inject(User);
 
  accountId = this.userService.currentUserId();
  private readonly relatedAccountNames = signal(new Map<number, string>());


  displayedColumns: string[] = ['timestamp', 'type', 'amount'];

 
  dataSource = new MatTableDataSource<Transaction>([]);


  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;


  expandedTransaction: Transaction | null = null;

  constructor() {
   
    effect(() => {
      const transactions = this.bankService.transactions();
      this.dataSource.data = transactions;
      untracked(() => this.loadPartyNames(transactions));
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }


  applyFilter(event: Event): void {
    const input = event.target as HTMLInputElement;

    this.dataSource.filter = input.value.trim().toLowerCase();

  
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  /*
   * Change:
   *
   * 2020-07-08 14:40:06-07
   *
   * into:
   *
   * Jul 08, 2020
   */
  formatDate(timestamp: string): string {
    const date = timestamp.substring(0, 10);

    const [year, month, day] = date.split('-');

    const months = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ];

    const monthName = months[Number(month) - 1];

    return `${monthName} ${day}, ${year}`;
  }


  // Other account on a transfer. accountId is the sender, relatedAccountId is the recipient.
  // Builds the first part of the message based on transfer type (EX: Sent to: {FirstName}, {LastName})
  transferParty(transaction: Transaction): { label: string; accountId: number } | null {
    if (transaction.type.toLocaleLowerCase() !== 'transfer') {
      return null;
    }
    if (transaction.accountId === this.accountId && transaction.relatedAccountId != null) {
      return { label: 'Sent to:', accountId: transaction.relatedAccountId };
    }
    if (transaction.relatedAccountId === this.accountId) {
      return { label: 'Received from:', accountId: transaction.accountId };
    }
    return null;
  }

  //Formats a line for related party 
  formatPartyName(accountId: number): string {
    const name = this.relatedAccountNames().get(accountId);
    const account = `(Account # ${accountId})`;
    return name ? `${name} ${account}` : account;
  }

  //Loads related account names for each Transaction (So we can easily populate Sent to/Recieved from on transfers)
  private loadPartyNames(transactions: Transaction[]): void {
    for (const transaction of transactions) {
      const id = this.transferParty(transaction)?.accountId;
      if (id == null || this.accountNames().has(id)) {
        continue;
      }
      this.accountNames.update((current) => new Map(current).set(id, ''));
      this.bankService.lookupAccount(id).subscribe({
        next: (account) => {
          const name = [account.firstName, account.lastName].join(', ') || account.username;
          this.accountNames.update((current) => new Map(current).set(id, name));
        },
      });
    }
  }

  getAmountClass(transaction: Transaction): string {
  
    if (transaction.type.toLocaleLowerCase() === 'deposit') {
      return 'amount-positive';
    }

   
    if (transaction.type.toLocaleLowerCase() === 'withdraw') {
      return 'amount-negative';
    }

  
    if (transaction.type.toLocaleLowerCase() === 'transfer') {
    
      if (transaction.accountId === this.accountId) {
        return 'amount-negative';
      }

  
      if (transaction.relatedAccountId === this.accountId) {
        return 'amount-positive';
      }
    }

    return '';
  }


  toggleTransaction(transaction: Transaction): void {
    
    if (this.isExpanded(transaction)) {
      
      this.expandedTransaction = null;
    } else {
      
      this.expandedTransaction = transaction;
    }
  }

 
  isExpanded(transaction: Transaction): boolean {
    return this.expandedTransaction != null
      && this.expandedTransaction.transactionId === transaction.transactionId;
  }
}
