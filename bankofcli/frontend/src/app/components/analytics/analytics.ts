import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Transaction } from '../../contracts/transaction';
import { getTransactionsByAccountId } from '../../mock/api';
import { BankService } from '../../service/bank.service';

@Component({
  imports: [CommonModule],
  selector: 'app-analytics',
  styleUrl: './analytics.css',
  templateUrl: './analytics.html',
})
export class Analytics {

  bankService = inject(BankService);
  transactions = this.bankService.transactions;

  moneyIn = computed(() => {
    let total = 0;

    for (const transaction of this.transactions()) {
      if (transaction.type == 'deposit') {
        total += transaction.amount;
      }
    }

    return total;
  });

  moneyOut = computed(() => {
    let total = 0;

    for (const transaction of this.transactions()) {
      if (transaction.type == 'withdraw' || transaction.type == 'transfer') {
        total += transaction.amount;
      }
    }

    return total;
  });

  netThisMonth = computed(() => this.moneyIn() - this.moneyOut());

  //transactions: Transaction[] = [];
  // moneyIn = 0;
  // moneyOut = 0;
  // netThisMonth = 0;

  // constructor() {
  //   this.transactions = getTransactionsByAccountId(4);

  //     for (const transaction of this.transactions) {
  //       if(transaction.type == 'deposit') {
  //         this.moneyIn += transaction.amount;
  //       }

  //       if(transaction.type == 'withdraw' || transaction.type == 'transfer') {
  //         this.moneyOut += transaction.amount;
  //       }
  //       this.netThisMonth = this.moneyIn - this.moneyOut;

  //   }
  // }


}
