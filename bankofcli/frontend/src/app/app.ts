import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { User } from './service/user';

@Component({
  imports: [RouterOutlet, Navbar],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('frontend');
  private readonly userService = inject(User);
  
  //Restores CSRF token when page is refreshed
  constructor() {
    this.userService.restoreSession().subscribe();
  }
}