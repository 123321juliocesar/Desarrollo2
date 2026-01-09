import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { loginService } from '../../core/services/loginService';
import { CartService } from '../../core/services/cart.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  //cartCount$: Observable<number>;

  constructor(
    public authService: loginService,
    //private cartService: CartService,
    private router: Router
  ) {
    //this.cartCount$ = this.cartService.cartCount$;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/home']);
  }
}
