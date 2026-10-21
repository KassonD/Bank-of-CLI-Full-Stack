import { Component, inject } from '@angular/core';
import { AccountOverview } from '../account-overview/account-overview';
import { Analytics } from '../analytics/analytics';
import { Transaction } from '../transaction/transaction';
import { History } from '../history/history';
import { BankService } from '../../service/bank.service';
import { User } from '../../service/user';
import { ToastService } from '../../service/toast';
import { httpErrorMessage } from '../../errors/http.errors';

@Component({
  imports: [AccountOverview, Analytics, Transaction, History],
  selector: 'app-dashboard',
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class Dashboard {
  private readonly bankService = inject(BankService);
  private readonly userService = inject(User);
  private readonly toastService = inject(ToastService);

  // Get the ID of the account that logged in.
  accountId = this.userService.currentUserId();

  ngOnInit() {
    if (this.accountId == null) {
      return;
    }
    this.bankService.loadAccount(this.accountId).subscribe({
      error: (err) => this.toastService.error(httpErrorMessage(err, 'Something went wrong. Please try again.')),
    });
  }
}
