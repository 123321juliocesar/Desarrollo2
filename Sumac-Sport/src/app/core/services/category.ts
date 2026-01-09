import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CategoryListResponse } from '../models/api-response.model';

@Injectable({
    providedIn: 'root',
})
export class CategoryService {
    private apiURL = 'http://localhost:8080/category';

    constructor(private http: HttpClient) { }

    obtenerCategorias(): Observable<CategoryListResponse> {
        return this.http.get<CategoryListResponse>(`${this.apiURL}/list`);
    }
}
