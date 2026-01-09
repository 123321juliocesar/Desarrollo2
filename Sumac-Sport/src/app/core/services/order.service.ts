import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private apiUrl = 'http://localhost:8080/api/orders';

    constructor(
        private http: HttpClient,
        @Inject(PLATFORM_ID) private platformId: Object
    ) { }

    createOrder(orderData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/create`, orderData);
    }

    getUserOrders(userId: string): Observable<any> {
        return this.http.get(`${this.apiUrl}/user/${userId}`);
    }

    getMyOrders(): Observable<any> {
        if (!isPlatformBrowser(this.platformId)) {
            return throwError(() => new Error('Not in browser'));
        }

        const currentUserStr = localStorage.getItem('currentUser');
        if (!currentUserStr) {
            return throwError(() => new Error('Usuario no autenticado'));
        }

        try {
            const currentUser = JSON.parse(currentUserStr);
            const userId = currentUser?.idUser;

            if (!userId) {
                return throwError(() => new Error('Usuario no tiene ID'));
            }

            console.log('Fetching orders for user:', userId);
            return this.http.get(`${this.apiUrl}/user/${userId}`);
        } catch (e) {
            return throwError(() => new Error('Error al parsear datos de usuario'));
        }
    }

    getOrderById(orderId: string): Observable<any> {
        return this.http.get(`${this.apiUrl}/${orderId}`);
    }
}
