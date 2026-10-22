import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
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
    const accountId = this.bankService.account()?.id;
    let total = 0;

    for (const transaction of this.transactions()) {
      const type = transaction.type.toLocaleLowerCase();
      if (type === 'deposit') {
        total += transaction.amount;
      }
      if (type === 'transfer' && transaction.relatedAccountId === accountId) {
        total += transaction.amount;
      }
    }

    return total;
  });

  moneyOut = computed(() => {
    const accountId = this.bankService.account()?.id;
    let total = 0;

    for (const transaction of this.transactions()) {
      const type = transaction.type.toLocaleLowerCase();
      if (type === 'withdraw') {
        total += transaction.amount;
      }
      if (type === 'transfer' && transaction.accountId === accountId) {
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
