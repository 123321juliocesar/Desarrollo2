import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class PaymentService {
    private apiUrl = 'http://localhost:8080/api/payments';


    constructor(private http: HttpClient) { }

    createPaypalOrder(orderId: string, returnUrl: string, cancelUrl: string): Observable<any> {
        const body = {
            idOrder: orderId,
            returnUrl: returnUrl,
            cancelUrl: cancelUrl
        };
        return this.http.post(`${this.apiUrl}/create`, body);
    }

    capturePaypalOrder(token: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/capture/${token}`, {});
    }

    // Alternative capture if backend uses query params or different path
    completePayment(transactionId: string): Observable<any> {
        return this.http.get(`${this.apiUrl}/cancel?paymentId=${transactionId}`); // Placeholder
    }
}
