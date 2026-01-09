import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AdminOrderService {
    private apiUrl = 'http://localhost:8080/api/orders';

    constructor(private http: HttpClient) { }

    getAllOrders(): Observable<any> {
        return this.http.get(`${this.apiUrl}/all`);
    }

    getOrdersByStatus(status: string): Observable<any> {
        return this.http.get(`${this.apiUrl}/status/${status}`);
    }

    updateOrderStatus(orderId: string, newStatus: string): Observable<any> {
        return this.http.put(
            `${this.apiUrl}/${orderId}/status`,
            null,
            { params: { newStatus } }
        );
    }
}
