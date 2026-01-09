import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BrandResponse, BrandListResponse } from '../models/api-response.model';

@Injectable({
    providedIn: 'root',
})
export class BrandService {
    private apiURL = 'http://localhost:8080/brand';

    constructor(private http: HttpClient) { }

    obtenerMarcaPorId(id: string): Observable<BrandResponse> {
        return this.http.get<BrandResponse>(`${this.apiURL}/${id}`);
    }

    obtenerMarcas(): Observable<BrandListResponse> {
        return this.http.get<BrandListResponse>(`${this.apiURL}/list`);
    }
}
