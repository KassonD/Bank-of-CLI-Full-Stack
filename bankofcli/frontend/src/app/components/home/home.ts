import { Component, DestroyRef, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  imports: [RouterLink],
  selector: 'home',
  styleUrl: './home.css',
  templateUrl: './home.html',
})
export class Home {

  protected readonly showLogin = signal(
    sessionStorage.getItem('authenticated') !== 'true'
  );
  
  private readonly router = inject(Router);

  constructor() {
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      takeUntilDestroyed(inject(DestroyRef)),
    ).subscribe(() => {
      this.showLogin.set(sessionStorage.getItem('authenticated') !== 'true');
    });
  }
}
