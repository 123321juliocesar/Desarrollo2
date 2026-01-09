import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { loginService } from '../../core/services/loginService';
import { Cart, CartItem } from '../../core/models/cart.model';
import { CartItemComponent } from './components/cart-item/cart-item.component';
import { OrderSummaryComponent } from './components/order-summary/order-summary.component';

@Component({
    selector: 'app-cart',
    standalone: true,
    imports: [CommonModule, CartItemComponent, OrderSummaryComponent],
    templateUrl: './cart.component.html',
    styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
    cart: Cart | null = null;
    loading: boolean = true;
    isEmpty: boolean = false;

    constructor(
        private cartService: CartService,
        private authService: loginService,
        private router: Router,
        @Inject(PLATFORM_ID) private platformId: Object
    ) { }

    ngOnInit() {
        this.cartService.cart$.subscribe(cart => {
            this.cart = cart;
            this.isEmpty = !cart || !cart.items || cart.items.length === 0;
            this.loading = false;
        });

        if (this.authService.isLoggedIn()) {
            const userId = this.authService.getUserId();
            if (userId) this.cartService.loadCart(userId);
        } else {
            // Redirect to login or just show empty? 
            // Requirement says "Consumir el endpoint... del usuario autenticado".
            // It's better to show empty state with "Login to view cart" or just empty.
            // For now, let's assume they might stick around, but the service handles logged out state as null cart.
            this.loading = false;
        }
    }

    updateQuantity(item: CartItem, newQuantity: number) {
        if (!this.cart) return;
        this.cartService.updateCartItem(item.idCartItem, {
            idUser: this.cart.idUser,
            quantity: newQuantity
        }).subscribe();
    }

    removeItem(item: CartItem) {
        if (!this.cart) return;
        if (isPlatformBrowser(this.platformId)) {
            if (confirm('¿Estás seguro de que deseas eliminar este producto del carrito?')) {
                this.cartService.removeCartItem(item.idCartItem, this.cart.idUser).subscribe();
            }
        }
    }

    proceedToPayment() {
        if (isPlatformBrowser(this.platformId)) {
            // alert('Funcionalidad de pago próximamente.');
            this.router.navigate(['/checkout']);
        }
    }

    continueShopping() {
        this.router.navigate(['/home']);
    }
}
