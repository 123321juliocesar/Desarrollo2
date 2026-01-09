import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { PaymentService } from '../../core/services/payment.service';
import { loginService } from '../../core/services/loginService';
import { OrderSummaryComponent } from '../cart/components/order-summary/order-summary.component';
import { Cart, CartItem } from '../../core/models/cart.model';

@Component({
    selector: 'app-checkout',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, OrderSummaryComponent],
    templateUrl: './checkout.component.html',
    styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
    checkoutForm: FormGroup;
    cart: Cart | null = null;
    currentStep: number = 1;
    loading: boolean = false;
    userId: string | null = null;

    constructor(
        private fb: FormBuilder,
        private cartService: CartService,
        private orderService: OrderService,
        private paymentService: PaymentService,
        private authService: loginService,
        private router: Router,
        @Inject(PLATFORM_ID) private platformId: Object
    ) {
        this.checkoutForm = this.fb.group({
            address: ['', Validators.required],
            city: ['', Validators.required],
            province: ['', Validators.required],
            zipCode: ['', Validators.required],
            country: ['Peru', Validators.required]
        });
    }

    ngOnInit() {
        if (isPlatformBrowser(this.platformId)) {
            if (!this.authService.isLoggedIn()) {
                this.router.navigate(['/login']); // Or handle guest checkout
                return;
            }
            this.userId = this.authService.getUserId();
            this.loadCart();
            this.loadUserData();
        }
    }

    loadCart() {
        this.cartService.cart$.subscribe(cart => {
            this.cart = cart;
            if (!cart || cart.items.length === 0) {
                this.router.navigate(['/cart']);
            }
        });
        if (this.userId) {
            this.cartService.loadCart(this.userId);
        }
    }

    loadUserData() {
        // Ideally fetch user address from profile service
        // For now, leaving empty or implementing if user service has address
    }

    get f() { return this.checkoutForm.controls; }

    proceedToPayment() {
        if (this.checkoutForm.invalid) {
            return;
        }
        this.currentStep = 2;
    }

    confirmOrder() {
        if (!this.cart || !this.userId) return;
        this.loading = true;

        const orderData = {
            idUser: this.userId,
            shippingAddress: this.checkoutForm.value.address,
            shippingCity: this.checkoutForm.value.city,
            shippingProvince: this.checkoutForm.value.province,
            shippingPostalCode: this.checkoutForm.value.zipCode,
            shippingCountry: this.checkoutForm.value.country,
            total: this.cart.total
        };

        // 1. Create Order
        this.orderService.createOrder(orderData).subscribe({
            next: (orderResponse) => {
                // 2. Create PayPal Payment
                const returnUrl = 'http://localhost:4200/order-confirmation'; // Adjust
                const cancelUrl = 'http://localhost:4200/checkout';

                this.paymentService.createPaypalOrder(orderResponse.idOrder, returnUrl, cancelUrl)
                    .subscribe({
                        next: (paymentResponse) => {
                            if (paymentResponse.approvalUrl) {
                                window.location.href = paymentResponse.approvalUrl;
                            } else {
                                alert('Error initiating PayPal payment');
                                this.loading = false;
                            }
                        },
                        error: (err) => {
                            console.error('Payment Error', err);
                            this.loading = false;
                            alert('Error creating payment');
                        }
                    });
            },
            error: (err) => {
                console.error('Order Error', err);
                this.loading = false;
                alert('Error creating order');
            }
        });
    }

    goBack() {
        this.currentStep = 1;
    }
}
