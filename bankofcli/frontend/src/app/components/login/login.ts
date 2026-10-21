import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../service/user';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../service/toast';
import { HttpErrorResponse } from '@angular/common/http';
import { httpErrorMessage } from '../../errors/http.errors';

/**
 * Handles user login functionality.
 * Binds input fields for username and password, and attempts authentication
 * using the User service. Navigates to the home page on success, or displays
 * an error message on failure.
 */
@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  usernameInput = '';
  passwordInput = '';
  confirmPasswordInput = '';

  firstNameInput = '';
  lastNameInput = '';



  errorMessage = signal('');

  private router = inject(Router);
  private userService = inject(User);
  private toastService = inject(ToastService);
  private mode = signal("login");
  

  /**
   * Attempts to log in using the provided credentials.
   * Navigates to the home page if successful, otherwise sets an error message.
   */
  attemptLogin() {
    this.errorMessage.set('');
    this.userService.login(this.usernameInput, this.passwordInput).subscribe({
      next: () => {this.router.navigate(['dashboard']);},
      error: (err) => {
        const message = err instanceof HttpErrorResponse && err.status === 401
          ? 'Wrong username or password.'
          : httpErrorMessage(err, 'Something went wrong. Please try again.');
        this.errorMessage.set(message);
      }
    });
  }

  setMode(newMode: 'login' | 'register') {
    this.mode.set(newMode);
    this.errorMessage.set('');
    this.passwordInput = '';
    this.confirmPasswordInput = '';
  }
  
  /**
   * Attempts to register using the provided credentials.
   * Navigates to the home page if successful, otherwise sets an error message.
   */
  attemptRegister() {
    this.errorMessage.set('');

    if (!this.firstNameInput || !this.lastNameInput) {
      this.errorMessage.set('first and last name are required.');
      return;
    }

    if (!this.usernameInput || !this.passwordInput) {
      this.errorMessage.set('Username and password are required.');
      return;
    }
    if (this.passwordInput !== this.confirmPasswordInput) {
      this.errorMessage.set('Passwords do not match.');
      return;
    }

    this.userService.register(this.usernameInput, this.passwordInput, this.firstNameInput, this.lastNameInput).subscribe({
      next: () => {
        this.toastService.success('Account succesfully registered, you may login with it now');
        this.setMode('login');
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage.set(err.status === 409 ? 'That username is already taken.'
          : httpErrorMessage(err, 'Something went wrong. Please try again.'));
      }
    });
  }

  async submit(){
    if (this.mode()==="login"){
      this.attemptLogin();
    }
    else
      this.attemptRegister();
  }
}