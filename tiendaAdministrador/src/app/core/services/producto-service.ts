import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProductoCreateRequest } from '../model/producto-request';

@Injectable({
  providedIn: 'root',
})
export class ProductoService {

  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }


  crearProducto(request: ProductoCreateRequest) {
    return this.http.post(
      `${this.apiUrl}/product/create`,
      request,
      { headers: { 'Content-Type': 'application/json' } }
    );
  }
  obtenerProductos() {
    return this.http.get(`${this.apiUrl}/product/list`);
  }

  obtenerProductoPorId(id: string) {
    return this.http.get(`${this.apiUrl}/product/${id}`);
  }

  actualizarProducto(id: string, request: any) {
    return this.http.put(`${this.apiUrl}/product/update/${id}`, request);
  }

  eliminarProducto(id: number) {
    return this.http.delete(`${this.apiUrl}/product/delete/${id}`);
  }

  ObtenerCategorias() {
    return this.http.get(`${this.apiUrl}/category/list`);
  }

  ObtenerMarcas() {
    return this.http.get(`${this.apiUrl}/brand/list`);
  }
}

