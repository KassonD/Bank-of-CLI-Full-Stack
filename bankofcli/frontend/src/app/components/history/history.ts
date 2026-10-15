import { AfterViewInit, Component, effect, inject, ViewChild } from '@angular/core';

import { CurrencyPipe } from '@angular/common';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';

import { MatSort, MatSortModule } from '@angular/material/sort';

import { MatFormFieldModule } from '@angular/material/form-field';

import { MatInputModule } from '@angular/material/input';

import { Transaction } from '../../contracts/transaction';
import { BankService } from '../../service/bank.service';

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

 
  accountId = Number(sessionStorage.getItem('accountId') || 0);


  displayedColumns: string[] = ['timestamp', 'type', 'amount'];

 
  dataSource = new MatTableDataSource<Transaction>([]);


  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;


  expandedTransaction: Transaction | null = null;

  constructor() {
   
    effect(() => {
      this.dataSource.data = this.bankService.transactions();
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


  getAmountClass(transaction: Transaction): string {
  
    if (transaction.type === 'deposit') {
      return 'amount-positive';
    }

   
    if (transaction.type === 'withdraw') {
      return 'amount-negative';
    }

  
    if (transaction.type === 'transfer') {
    
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
    return this.expandedTransaction?.id === transaction.id;
  }
}
