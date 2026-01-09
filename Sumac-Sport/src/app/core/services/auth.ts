import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient) { }

  crearUsuario(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  obtenerUsuarios(): Observable<any> {
    return this.http.get(`${this.apiUrl}/list`);
  }

  obtenerUsuarioPorId(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  actualizarUsuario(id: string, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${id}`, data);
  }

}
