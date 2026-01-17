import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((err) => {
        const message = err?.error?.message || err?.message || 'Error en la petición';

        switch (err?.status) {
          case 400:
            if (isPlatformBrowser(this.platformId)) alert(`Solicitud inválida: ${message}`);
            break;
          case 401:
            if (isPlatformBrowser(this.platformId)) {
              alert('No autorizado. Por favor inicia sesión.');
              this.router.navigate(['/login']);
            } else {
              this.router.navigate(['/login']);
            }
            break;
          case 403:
            if (isPlatformBrowser(this.platformId)) alert('Acceso denegado.');
            break;
          case 404:
            if (isPlatformBrowser(this.platformId)) alert('Recurso no encontrado.');
            break;
          case 500:
            if (isPlatformBrowser(this.platformId)) alert('Error del servidor, intenta más tarde.');
            break;
          default:
            if (isPlatformBrowser(this.platformId)) alert(message);
        }

        return throwError(() => err);
      })
    );
  }
}
