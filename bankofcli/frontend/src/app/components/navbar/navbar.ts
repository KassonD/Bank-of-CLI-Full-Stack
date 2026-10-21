import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive} from '@angular/router';
import { User } from '../../service/user';

@Component({
  imports: [RouterLink, RouterLinkActive],
  selector: 'app-navbar',
  styleUrl: './navbar.css',
  templateUrl: './navbar.html',
})
export class Navbar {
  protected readonly userService = inject(User);

  private readonly router = inject(Router);

  private logout() {
    this.userService.logout().subscribe();
    this.router.navigate(['/'], { onSameUrlNavigation: 'reload' });
  }

}
