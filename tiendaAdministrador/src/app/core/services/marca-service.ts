import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MarcaService {

 private apiUrl = 'http://localhost:8080/brand';

   constructor(private http: HttpClient) {}

   crearMarca(marca: any): Observable<any> {

     const body = {
       dto: {
         brand: marca
       }
     };

     return this.http.post(`${this.apiUrl}/create`, body);
   }
}
