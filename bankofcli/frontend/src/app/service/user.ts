import { Injectable, signal } from '@angular/core';
import { Account } from '../contracts/account';
import { addAccount, getAccounts } from '../mock/api';


export type UserErrorCode =
  | 'ACCOUNT_EXISTS'
  | 'ACTIVE_DOES_NOT_EXIST';

export class UserError extends Error {
  constructor(public readonly code: UserErrorCode, message: string) {
    super(message);
    this.name = 'UserError';
  }
}
/**
 * User service that provides basic mock authentication functionality.
 * This is intended for learning purposes and does not represent secure authentication.
 */
@Injectable({
  providedIn: 'root' // Makes this service available application-wide without needing to register it in a module.
})
export class User {

  private readonly SESSION_KEY = 'accountId';
  loggedIn = signal



  /**
   * Attempts to log in a user by comparing input credentials to the hardcoded ones.
   * If successful, sets a flag in sessionStorage to indicate authentication.
   * 
   * @param username - The username entered by the user.
   * @param password - The password entered by the user.
   * @returns boolean - True if credentials match, false otherwise.
   */

  login(username: string, password: string): boolean {

    

    const account = getAccounts().find(
      
      a => username === a.username && password === a.password
      );
      if(!account){
        return false
      }
      sessionStorage.setItem(this.SESSION_KEY, String(account.id));
      sessionStorage.setItem('authenticated', 'true');

      return true;
  }


  register(username: string, password: string, firstName: string, lastName: string): void {
    
    const exists = getAccounts().find(
      a => username === a.username
    );

    if(exists){
      throw new UserError('ACCOUNT_EXISTS', "the user provided already exists. ");
    }
    
    const created = addAccount({username, password, firstName, lastName});


  }

  logout(): void {
    sessionStorage.clear();
  }

  /**
   * Checks if the user is currently logged in by reading sessionStorage.
   * 
   * @returns boolean - True if the user is authenticated, false otherwise.
   */
  isLoggedIn(): boolean {
    const isAuthenticated = sessionStorage.getItem('authenticated');
    return isAuthenticated === 'true';
  }
}
