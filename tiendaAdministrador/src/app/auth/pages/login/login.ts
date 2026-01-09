import { Component,OnInit } from '@angular/core';
import {AuthService} from '../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LoginRequest } from './model/login.model';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {

  email: string = '';
  password: string = '';
  codigo: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isLogged()) {
      this.router.navigate(['/home']);
    }
  }

  login(): void {

  const request: LoginRequest = {
    dto: {
      email: this.email,
      password: this.password
    }
  };

  this.authService.loginRequest(request).subscribe({
    next: (response) => {
      console.log(response);

      if (response.user) {
        this.authService.login();
        this.router.navigate(['/home']);
      } else {
        alert(response.message || 'Credenciales incorrectas');
      }
    },
    error: (error) => {
      console.error('Error login:', error);
      alert('Error al conectar con el servidor');
    }
  });
}
}
