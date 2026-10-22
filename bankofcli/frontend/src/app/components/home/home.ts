import { Component, DestroyRef, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { User } from '../../service/user';

@Component({
  imports: [RouterLink],
  selector: 'home',
  styleUrl: './home.css',
  templateUrl: './home.html',
})
export class Home {
  protected readonly userService = inject(User);
}
