import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { User } from '../service/user';
import { map } from 'rxjs';

/**
 * Route guard that checks if the user is authenticated before allowing access to a route.
 * If the user is not logged in, they are redirected to the login page.
 * 
 * This guard uses Angular's functional `CanActivateFn` approach introduced in Angular 15+.
 */
export const authenticationGuard: CanActivateFn = (route, state) => {
  // Inject the User service to check authentication status.
  const userService = inject(User);

  // Inject the Router to perform navigation if the user is not authenticated.
  const router = inject(Router);

  // restoreSession() emits true when GET /api/auth/me succeeds and false when it fails
  // when authenticated is true, the dashboard loads otherwise we redirect to login page
  return userService.restoreSession().pipe(
    map((authenticated) => authenticated || router.createUrlTree(['/login'])),
  );
};