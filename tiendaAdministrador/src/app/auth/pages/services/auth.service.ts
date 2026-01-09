import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { LoginRequest, LoginResponse } from '../login/model/login.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  apiURL: string = 'http://localhost:8080/user/login';

  constructor(private http: HttpClient,
              @Inject(PLATFORM_ID) private platformId: Object) {}


  loginRequest(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.apiURL, data);
  }

  login() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('logged', 'true');
    }
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('logged');
    }
  }

  isLogged(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('logged') === 'true';
    }
    return false;
  }
}

