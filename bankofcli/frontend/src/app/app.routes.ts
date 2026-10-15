import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Dashboard } from './components/dashboard/dashboard';
import { authenticationGuard } from './guards/authentication-guard';
import { Home } from './components/home/home';
import { NotFound } from './components/not-found/not-found';

/**
 * Defines the application's route configuration.
 * Includes paths for login and home, with route guarding applied to the home route.
 * Redirects empty path to the login page by default.
 */
export const routes: Routes = [

  {
    path: "login",
    component: Login
  },
  {
    path: "dashboard",
    component: Dashboard,
    canActivate: [authenticationGuard] // Protects the home route from unauthenticated access
  },
  {
    path: "",
    component: Home,
    pathMatch: "full" // Ensures full path match before redirecting to avoid an infinite loop
  },
  {
    path: "**",
    component: NotFound
  }
];

