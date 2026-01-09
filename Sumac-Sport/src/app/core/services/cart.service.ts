import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { Cart, AddToCartRequest, UpdateCartItemRequest } from '../models/cart.model';
import { loginService } from './loginService';

@Injectable({
    providedIn: 'root'
})
export class CartService {
    private apiUrl = 'http://localhost:8080/api/cart';

    private cartSubject = new BehaviorSubject<Cart | null>(null);
    public cart$ = this.cartSubject.asObservable();

    private cartCountSubject = new BehaviorSubject<number>(0);
    public cartCount$ = this.cartCountSubject.asObservable();

    constructor(
        private http: HttpClient,
        private authService: loginService,
        @Inject(PLATFORM_ID) private platformId: Object
    ) {
        // Subscribe to auth changes to clear cart on logout
        this.authService.currentUser.subscribe(user => {
            if (!user) {
                this.cartSubject.next(null);
                this.cartCountSubject.next(0);
            }
            // Note: We don't automatically load cart on login anymore for performance
            // Components that need cart data should call loadCart() explicitly
        });
    }

    loadCart(userId: string): void {
        if (!isPlatformBrowser(this.platformId)) return;

        this.http.get<Cart>(`${this.apiUrl}/${userId}`).pipe(
            tap(cart => {
                this.updateCartState(cart);
            }),
            catchError(err => {
                console.error('Error loading cart', err);
                return throwError(() => err);
            })
        ).subscribe();
    }

    addToCart(request: AddToCartRequest): Observable<Cart> {
        console.log('CartService.addToCart called with:', request);
        return this.http.post<Cart>(`${this.apiUrl}/add`, request).pipe(
            tap(cart => {
                console.log('CartService.addToCart success:', cart);
                this.updateCartState(cart);
            }),
            catchError(err => {
                console.error('CartService.addToCart error:', err);
                return throwError(() => err);
            })
        );
    }

    updateCartItem(idCartItem: string, request: UpdateCartItemRequest): Observable<Cart> {
        return this.http.put<Cart>(`${this.apiUrl}/item/${idCartItem}`, request).pipe(
            tap(cart => this.updateCartState(cart))
        );
    }

    removeCartItem(idCartItem: string, idUser: string): Observable<any> {
        const params = new HttpParams().set('idUser', idUser);
        return this.http.delete(`${this.apiUrl}/item/${idCartItem}`, { params }).pipe(
            tap(() => {
                // After deletion, we should reload the cart to get updated totals
                this.loadCart(idUser);
            })
        );
    }

    clearCart(idUser: string): Observable<any> {
        return this.http.delete(`${this.apiUrl}/clear/${idUser}`).pipe(
            tap(() => {
                this.cartSubject.next(null);
                this.cartCountSubject.next(0);
            })
        );
    }

    private updateCartState(cart: Cart | null) {
        this.cartSubject.next(cart);
        this.cartCountSubject.next(cart?.totalItems || 0);
    }

    getCartCount(): number {
        return this.cartCountSubject.value;
    }
}
