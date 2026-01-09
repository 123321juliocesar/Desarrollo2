import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { loginService } from '../../../core/services/loginService';
import { LoginRequest } from '../../../core/models/login.model';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: loginService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  login() {
    if (this.form.invalid) return;

    const request: LoginRequest = {
      dto: {
        email: this.form.value.email!,
        password: this.form.value.password!
      }
    };

    this.authService.login(request).subscribe({
      next: (resp) => {
        console.log('Login OK:', resp);

        if (resp.user) {
          this.authService.saveUser(resp.user);
          if (isPlatformBrowser(this.platformId)) {
            alert('Bienvenido ' + resp.user.firstName);
          }
          // Redirigir al home
          this.router.navigate(['/home']);
        } else {
          if (isPlatformBrowser(this.platformId)) {
            alert('Login exitoso pero no se recibieron datos de usuario');
          }
        }
      },
      error: (err) => {
        console.error(err);
        if (isPlatformBrowser(this.platformId)) {
          alert('Error al iniciar sesión: ' + (err.error?.message || 'Credenciales inválidas'));
        }
      }
    });
  }
}
