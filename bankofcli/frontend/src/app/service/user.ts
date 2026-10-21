import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of, shareReplay, switchMap, tap } from 'rxjs';
import { CsrfToken } from './csrf-token';

export interface AuthResult {
  userName: string,
  id: number,
  firstName: string,
  lastName: string,
  balance: number
}

@Injectable({
  providedIn: 'root' // Makes this service available application-wide without needing to register it in a module.
})
export class User {
  private session$: Observable<boolean> | null = null;
  private readonly http = inject(HttpClient);
  readonly isAuthenticated = signal(false);
  readonly currentUserId = signal<number | null>(null);
  readonly currentUserName = signal<string | null>(null);
  apiUrl: string = '/api/auth';

  private readonly csrf = inject(CsrfToken);

  // Fetches a CSRF token only when one is not already stored.
  private ensureCsrf(): Observable<unknown> {
    return this.csrf.getCurrent() ? of(null) : this.http.get<unknown>(`${this.apiUrl}/csrf`);
  }

  // Logs in, then loads a new CSRF token because login replaces the cookie, then loads the current user.
  login(userName: string, password: string): Observable<unknown> {
    return this.ensureCsrf().pipe(
      switchMap(() =>
        this.http.post<unknown>(`${this.apiUrl}/login`, { userName, password }),
      ),
      switchMap(() => this.http.get<unknown>(`${this.apiUrl}/csrf`)),
      switchMap(() => this.loadCurrentUser())
    );
  }

  // Reads GET /api/auth/me and stores the id, username, and authenticated flag.
  loadCurrentUser(): Observable<AuthResult> {
    return this.http.get<AuthResult>(`${this.apiUrl}/me`).pipe(
      tap((user) => {
        this.currentUserId.set(user.id);
        this.currentUserName.set(user.userName);
        this.isAuthenticated.set(true);
      }),
    );
  }

  // Creates an account. Does not start a session.
  register(userName: string, password: string, firstName: string, lastName: string): Observable<unknown> {
    return this.ensureCsrf().pipe(
      switchMap(() =>
        this.http.post<unknown>(`${this.apiUrl}/register`, { userName, password, firstName, lastName }),
      ),
    );
  }
  
  // Ends the session and drops the stored CSRF token, user id, and authenticated flag.
  logout(): Observable<unknown> {
    return this.ensureCsrf().pipe(
      switchMap(() => this.http.post<unknown>(`${this.apiUrl}/logout`, {})),
      tap(() => {
        this.csrf.clear();
        this.session$ = null;
        this.currentUserId.set(null);
        this.currentUserName.set(null);
        this.isAuthenticated.set(false);
      }),
    );
  }
  
  // On a fresh page load, fetches a CSRF token and then the current user. Repeated callers share one request.
  restoreSession(): Observable<boolean> {
    if (this.isAuthenticated()) {
      return of(true);
    }
    if (!this.session$) {
      this.session$ = this.http.get<unknown>(`${this.apiUrl}/csrf`).pipe(
        switchMap(() => this.loadCurrentUser()),
        map(() => true),
        catchError(() => {
          this.currentUserId.set(null);
          this.currentUserName.set(null);
          this.isAuthenticated.set(false);
          return of(false);
        }),
        shareReplay(1)
      );
    }
    return this.session$;
  }
}
