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



  errorMessage = '';
  successMessage = ''

  private router = inject(Router);
  private userService = inject(User);
  private mode = signal("login");
  

  /**
   * Attempts to log in using the provided credentials.
   * Navigates to the home page if successful, otherwise sets an error message.
   */
  attemptLogin() {
    this.errorMessage = '';
    const loginSuccessful = this.userService.login(this.usernameInput, this.passwordInput).subscribe({
      next: () => {this.router.navigate(['dashboard']);},
      error: (err) => {
        this.errorMessage = httpErrorMessage(err, 'login failed, please try again');
      }
    });
  }

  setMode(newMode: 'login' | 'register') {
    this.mode.set(newMode);
    this.errorMessage = '';
    this.successMessage = '';
    this.passwordInput = '';
    this.confirmPasswordInput = '';
  }
  
  /**
   * Attempts to register using the provided credentials.
   * Navigates to the home page if successful, otherwise sets an error message.
   */
  attemptRegister() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.firstNameInput || !this.lastNameInput) {
      this.errorMessage = 'first and last name are required.';
      return;
    }

    if (!this.usernameInput || !this.passwordInput) {
      this.errorMessage = 'Username and password are required.';
      return;
    }
    if (this.passwordInput !== this.confirmPasswordInput) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.userService.register(this.usernameInput, this.passwordInput, this.firstNameInput, this.lastNameInput).subscribe({
      next: () => {
        this.successMessage = 'Registration successful, you may now log in.';
        this.setMode('login');
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.status === 409 ? 'That username is already taken.'
          : httpErrorMessage(err, 'Something went wrong. Please try again.');
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