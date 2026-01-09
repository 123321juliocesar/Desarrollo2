import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartItem } from '../../../../core/models/cart.model';

@Component({
    selector: 'app-order-summary',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './order-summary.component.html',
    styleUrls: ['./order-summary.component.css']
})
export class OrderSummaryComponent {
    @Input() subtotal: number = 0;
    @Input() shippingCost: number = 0;
    @Input() total: number = 0;
    @Input() items: CartItem[] = [];
    @Input() showItems: boolean = false;
    @Input() hideButtons: boolean = false;

    @Output() proceedCheckout = new EventEmitter<void>();
    @Output() continueShopping = new EventEmitter<void>();

    onProceed() {
        this.proceedCheckout.emit();
    }

    onContinue() {
        this.continueShopping.emit();
    }
}
