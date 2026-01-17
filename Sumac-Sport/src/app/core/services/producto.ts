import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { ProductListResponse, ProductDetailResponse } from '../models/api-response.model';


@Injectable({
  providedIn: 'root',
})
export class ProductoService {
  private apiURL = 'https://desarrollo2-1.onrender.com/product';

  constructor(private http: HttpClient) { }

  /*obtenerProductos(): Observable<ProductListResponse> {
    return this.http.get<ProductListResponse>(`${this.apiURL}/list`); */

  obtenerProductos(filters?: {
    search?: string;
    idCategory?: string;
    idBrand?: string;
    sizes?: string;
    colors?: string;
    minPrice?: number;
    maxPrice?: number;
    sortBy?: string;
  }): Observable<ProductListResponse> {
    let params = new HttpParams();

    if (filters) {
      if (filters.search) params = params.set('search', filters.search);
      if (filters.idCategory) params = params.set('idCategory', filters.idCategory);
      if (filters.idBrand) params = params.set('idBrand', filters.idBrand);
      if (filters.sizes) params = params.set('sizes', filters.sizes);
      if (filters.colors) params = params.set('colors', filters.colors);
      if (filters.minPrice !== undefined) params = params.set('minPrice', filters.minPrice.toString());
      if (filters.maxPrice !== undefined) params = params.set('maxPrice', filters.maxPrice.toString());
      if (filters.sortBy) params = params.set('sortBy', filters.sortBy);
    }

    return this.http.get<ProductListResponse>(`${this.apiURL}/list`, { params });
  }

  obtenerProductoPorId(id: string): Observable<ProductDetailResponse> {
    return this.http.get<ProductDetailResponse>(`${this.apiURL}/${id}`);
  }

}
