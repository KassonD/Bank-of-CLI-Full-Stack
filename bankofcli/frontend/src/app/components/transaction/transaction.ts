import { Component, inject } from '@angular/core';
import { BankService } from '../../service/bank.service';
import { FormsModule, NgForm } from '@angular/forms'; 
import { ToastService } from '../../service/toast';
import { httpErrorMessage } from '../../errors/http.errors';

@Component({
  imports: [FormsModule],
  selector: 'app-transaction',
  styleUrl: './transaction.css',
  templateUrl: './transaction.html',
})
export class Transaction {
  private readonly bankService = inject(BankService);
  private readonly toastService = inject(ToastService);

  type: 'deposit' | 'withdraw' | 'transfer' = 'deposit';
  relatedAccountId: number | null = null;
  amount: number = 0.00;
  description: string = ''

  onSubmit(form: NgForm) {
    switch (this.type) {
      case 'deposit':
        this.bankService.deposit(this.amount, this.description).subscribe({
          next: value => {
            this.toastService.success("Deposit successful!"),
            this.resetForm(form);
          },
          error: err => this.toastService.error(httpErrorMessage(err, 'Something went wrong. Please try again.'))
      });
        break;
    
      case 'withdraw':
        this.bankService.withdraw(this.amount, this.description).subscribe({
          next: value => {
            this.toastService.success("Withdraw successful!")
            this.resetForm(form);
          },
          error: err => this.toastService.error(httpErrorMessage(err, 'Something went wrong. Please try again.'))
        });
        break;
    
      case 'transfer':
        this.bankService.transfer(this.relatedAccountId == null ? -1 : this.relatedAccountId, this.amount, this.description).subscribe({
          next: value => {
            this.toastService.success("Transfer successful!");
            this.resetForm(form);
          },
          error: err => this.toastService.error(httpErrorMessage(err, 'Something went wrong. Please try again.'))
        });
        break;
      
      default:
        break;
    }
  }

  private resetForm(form: NgForm) {
    form.resetForm({
      description: '',
      type: this.type,
      relatedAccountId: null,
      amount: 0.00,
    });
  }
}
