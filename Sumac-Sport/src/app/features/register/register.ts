import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { loginService } from '../../core/services/loginService';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register implements OnInit {

  crearRegistro: FormGroup;
  isEditMode: boolean = false;
  userId: string | null = null;

  private readonly ROL_PREDETERMINADO = "33a0b339-8ff8-4592-b0ca-671856b3ff15";

  constructor(
    private usuarioService: AuthService,
    private loginService: loginService,
    private route: ActivatedRoute,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {

    this.crearRegistro = new FormGroup({
      firstName: new FormControl('', Validators.required),
      lastName: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
      phone: new FormControl('', Validators.required),
      address: new FormControl(''),
      city: new FormControl(''),
      postalCode: new FormControl(''),
      province: new FormControl(''),
      country: new FormControl(''),
      birthDate: new FormControl(''),
      active: new FormControl(true),
    });
  }

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;

    // Check if we are in edit mode based on route path or data
    this.route.url.subscribe(url => {
      const path = url[0]?.path;
      if (path === 'editar-register') {
        this.isEditMode = true;
        this.userId = this.loginService.getUserId();
        this.initEditMode();
      }
    });
  }

  initEditMode() {
    if (!this.userId) {
      if (isPlatformBrowser(this.platformId)) {
        alert('Debes iniciar sesión para editar tu perfil');
      }
      this.router.navigate(['/login']);
      return;
    }

    // Disable email
    this.crearRegistro.get('email')?.disable();

    // Make password optional
    this.crearRegistro.get('password')?.clearValidators();
    this.crearRegistro.get('password')?.updateValueAndValidity();

    // Load user data
    this.usuarioService.obtenerUsuarioPorId(this.userId).subscribe({
      next: (resp) => {
        if (resp && resp.user) {
          const user = resp.user;
          this.crearRegistro.patchValue({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            phone: user.phone,
            address: user.address,
            city: user.city,
            postalCode: user.postalCode,
            province: user.province,
            country: user.country,
            birthDate: user.birthDate ? user.birthDate.split('T')[0] : '', // Format date for input type=date
            active: user.active
          });
        }
      },
      error: (err) => {
        console.error('Error loading user', err);
        if (isPlatformBrowser(this.platformId)) {
          alert('Error al cargar datos del usuario');
        }
      }
    });
  }

  enviarRegistro() {
    if (this.crearRegistro.invalid) {
      this.crearRegistro.markAllAsTouched();
      return;
    }

    const formRawValue = this.crearRegistro.getRawValue();

    if (this.isEditMode && this.userId) {
      // PREPARAR JSON PARA ACTUALIZACIÓN
      const updatePayload = {
        dto: {
          user: {
            idUser: this.userId,
            firstName: formRawValue.firstName,
            lastName: formRawValue.lastName,
            email: formRawValue.email, // Se envía el email (aunque esté deshabilitado en el form)
            phone: formRawValue.phone,
            address: formRawValue.address,
            city: formRawValue.city,
            postalCode: formRawValue.postalCode,
            province: formRawValue.province,
            country: formRawValue.country,
            birthDate: formRawValue.birthDate || null,
            password: formRawValue.password || null // Si está vacío, el backend no lo actualiza
          }
        }
      };

      this.usuarioService.actualizarUsuario(this.userId, updatePayload).subscribe({
        next: () => {
          if (isPlatformBrowser(this.platformId)) {
            alert("Usuario actualizado correctamente");
          }
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error(err);
          if (isPlatformBrowser(this.platformId)) {
            alert("Error al actualizar usuario");
          }
        }
      });

    } else {
      // PREPARAR FORMDATA PARA REGISTRO (MULTIPART)
      const formData = new FormData();
      formData.append("dto.user.idRole", this.ROL_PREDETERMINADO);
      formData.append("dto.user.firstName", formRawValue.firstName || "");
      formData.append("dto.user.lastName", formRawValue.lastName || "");
      formData.append("dto.user.email", formRawValue.email || "");
      formData.append("dto.user.password", formRawValue.password || "");
      formData.append("dto.user.phone", formRawValue.phone || "");
      formData.append("dto.user.address", formRawValue.address ?? "");
      formData.append("dto.user.city", formRawValue.city ?? "");
      formData.append("dto.user.postalCode", formRawValue.postalCode ?? "");
      formData.append("dto.user.province", formRawValue.province ?? "");
      formData.append("dto.user.country", formRawValue.country ?? "");
      formData.append("dto.user.birthDate", formRawValue.birthDate ?? "");
      formData.append("dto.user.active", "true");

      this.usuarioService.crearUsuario(formData).subscribe({
        next: () => {
          if (isPlatformBrowser(this.platformId)) {
            alert("Usuario creado correctamente");
          }
          this.crearRegistro.reset();
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.log(err);
          if (err.status === 409) {
            if (isPlatformBrowser(this.platformId)) {
              alert("El email ya está registrado");
            }
          } else {
            if (isPlatformBrowser(this.platformId)) {
              alert("Error en el registro");
            }
          }
        }
      });
    }
  }
}


