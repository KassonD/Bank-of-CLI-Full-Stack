import { Component, inject } from '@angular/core';
import { AccountOverview } from '../account-overview/account-overview';
import { Analytics } from '../analytics/analytics';
import { Transaction } from '../transaction/transaction';
import { History } from '../history/history';
import { BankService } from '../../service/bank.service';

@Component({
  imports: [AccountOverview, Analytics, Transaction, History],
  selector: 'app-dashboard',
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class Dashboard {
  private readonly bankService = inject(BankService);

  // Get the ID of the account that logged in.
  accountId = sessionStorage.getItem('accountId');

  ngOnInit() {
    // Load the currently logged-in account.
    this.bankService.loadAccount(Number(this.accountId || 0)).subscribe();
  }
}
