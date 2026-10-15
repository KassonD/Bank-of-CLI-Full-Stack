import { Component, Input, inject } from '@angular/core';
import { BankService } from '../../service/bank.service';
import { FormsModule, NgForm } from '@angular/forms'; 
import { ToastService } from '../../service/toast';

@Component({
  imports: [FormsModule],
  selector: 'app-transaction',
  styleUrl: './transaction.css',
  templateUrl: './transaction.html',
})
export class Transaction {
  private readonly bankService = inject(BankService);
  private readonly toastService = inject(ToastService);

  accountId = sessionStorage.getItem("accountId");

  type: 'deposit' | 'withdraw' | 'transfer' = 'deposit';
  relatedAccountId: number | null = null;
  amount: number = 0.00;
  description: string = ''

  

  // ngOnInit() {
  //   console.log("init");
  //   this.bankService.loadAccount(this.accountId).subscribe();
  // }

  onSubmit(form: NgForm) {
    console.log(this.accountId, this.type, this.relatedAccountId, this.amount, this.description);

    switch (this.type) {
      case 'deposit':
        this.bankService.deposit(this.amount, this.description).subscribe({
          next: value => {
            this.toastService.success("Deposit successful!"),
            this.resetForm(form);
          },
          error: err => this.toastService.error(err.message)
      });
        break;
    
      case 'withdraw':
        this.bankService.withdraw(this.amount, this.description).subscribe({
          next: value => {
            this.toastService.success("Withdraw successful!")
            this.resetForm(form);
          },
          error: err => this.toastService.error(err.message)
        });
        break;
    
      case 'transfer':
        this.bankService.transfer(this.relatedAccountId == null ? -1 : this.relatedAccountId, this.amount, this.description).subscribe({
          next: value => {
            this.toastService.success("Transfer successful!");
            this.resetForm(form);
          },
          error: err => this.toastService.error(err.message)
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
