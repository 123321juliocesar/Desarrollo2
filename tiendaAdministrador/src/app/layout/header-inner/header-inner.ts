import { Component, EventEmitter, Output } from '@angular/core';
import { AuthService } from '../../auth/pages/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header-inner',
  imports: [],
  templateUrl: './header-inner.html',
  styleUrl: './header-inner.css',
})
export class HeaderInner {
@Output() menuClick = new EventEmitter();

  constructor(
    private authService: AuthService,
    private router: Router

  ) {}

  toggleMenu() {
    this.menuClick.emit();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }
}
