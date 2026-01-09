import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartItem } from '../../../../core/models/cart.model';

@Component({
    selector: 'app-cart-item',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './cart-item.component.html',
    styleUrls: ['./cart-item.component.css']
})
export class CartItemComponent {
    @Input() item!: CartItem;
    @Output() quantityChange = new EventEmitter<number>();
    @Output() remove = new EventEmitter<void>();

    increment() {
        // Check stock limit if available
        const stock = this.item.variant.stock || 999;
        if (this.item.quantity < stock) {
            this.quantityChange.emit(this.item.quantity + 1);
        }
    }

    decrement() {
        if (this.item.quantity > 1) {
            this.quantityChange.emit(this.item.quantity - 1);
        }
    }

    removeItem() {
        this.remove.emit();
    }
}
