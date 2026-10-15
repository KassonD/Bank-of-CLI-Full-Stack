import { Component, DestroyRef, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive} from '@angular/router';
import { User } from '../../service/user';
import { getAccountById } from '../../mock/api';
import { filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  imports: [RouterLink, RouterLinkActive],
  selector: 'app-navbar',
  styleUrl: './navbar.css',
  templateUrl: './navbar.html',
})
export class Navbar {
  protected readonly userService = inject(User);

  userName = signal("");

  private readonly router = inject(Router);
  constructor() {
  //every time the route changes, the user name is refreshed
  this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntilDestroyed(inject(DestroyRef)),
      )
      .subscribe(() => this.refresh());
  }

  private refresh() {
    const stored = sessionStorage.getItem("accountId");
    if (!stored) {
      this.userName.set('');
      return;
    }
    const account = getAccountById(Number(stored));
    this.userName.set(account?.username ?? '');
  }

  private logout() {
    this.userService.logout();
    this.router.navigate(['/'], { onSameUrlNavigation: 'reload' });
  }

}
