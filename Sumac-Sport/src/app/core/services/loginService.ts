import { HttpClient } from '@angular/common/http';
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { LoginRequest, LoginResponse } from '../models/login.model';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class loginService {
  private apiUrl = 'http://localhost:8080/user/login';

  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    let storageUser = null;
    if (isPlatformBrowser(this.platformId)) {
      storageUser = JSON.parse(localStorage.getItem('currentUser') || 'null');
    }
    this.currentUserSubject = new BehaviorSubject<any>(storageUser);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  login(data: LoginRequest): Observable<LoginResponse> {
    // Observe full response to capture potential Authorization header token
    return this.http.post<LoginResponse>(this.apiUrl, data, { observe: 'response' as const }).pipe(
      map((resp) => {
        const body = resp.body as LoginResponse;
        const authHeader = resp.headers.get('Authorization') || resp.headers.get('authorization');
        if (body && body.user && authHeader) {
          // normalize token key on user object for interceptor
          (body.user as any).token = authHeader.replace(/^Bearer\s+/i, '');
        }
        return body;
      }),
      // keep compatibility if any tap-based side-effects are needed in future
      // no-op tap here to keep return type as Observable<LoginResponse>
      tap(() => {})
    );
  }

  saveUser(user: any): void {
    if (user) {
      if (isPlatformBrowser(this.platformId)) {
        localStorage.setItem('currentUser', JSON.stringify(user));
      }
      this.currentUserSubject.next(user);
    }
  }

  getUser(): any {
    return this.currentUserValue;
  }

  getUserId(): string | null {
    const user = this.currentUserValue;
    return user ? user.idUser : null;
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('currentUser');
    }
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.currentUserValue;
  }

}
