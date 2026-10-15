import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { User, UserError } from '../../service/user';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../service/toast';

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
  async attemptLogin() {
    this.errorMessage = '';
    const loginSuccessful = this.userService.login(this.usernameInput, this.passwordInput);
    if (loginSuccessful) {
      await this.router.navigate(['dashboard']);
    } else {
      this.errorMessage = 'login failed, please try again';
      
    }
  }

  setMode(newMode: 'login' | 'register') {
  this.mode.set(newMode);
  this.errorMessage = '';
  this.successMessage = '';
  this.passwordInput = '';
  this.confirmPasswordInput = '';
}

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

    try {
      this.userService.register(
        this.usernameInput,
        this.passwordInput,
        this.firstNameInput,
        this.lastNameInput
      );
      this.successMessage = 'Registration successful, you may now log in.';
    } catch (e) {
      if (e instanceof UserError && e.code === 'ACCOUNT_EXISTS') {
        this.errorMessage = 'That username is already taken.';
      } else {
        this.errorMessage = 'Something went wrong. Please try again.';
      }
    }
  }

  async submit(){
    if (this.mode()==="login"){
      this.attemptLogin();
    }
    else
      this.attemptRegister();
  }
}