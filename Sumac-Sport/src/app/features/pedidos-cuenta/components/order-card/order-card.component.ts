import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Order } from '../../../../core/models/order.model';

@Component({
    selector: 'app-order-card',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './order-card.component.html',
    styleUrl: './order-card.component.css',
})
export class OrderCardComponent {
    @Input() order!: Order;
    @Output() viewDetails = new EventEmitter<string>();

    getStatusBadgeClass(status: string): string {
        const statusMap: { [key: string]: string } = {
            'pending': 'badge-warning',
            'confirmed': 'badge-info',
            'shipped': 'badge-primary',
            'delivered': 'badge-success',
            'cancelled': 'badge-danger'
        };
        return statusMap[status] || 'badge-secondary';
    }

    getStatusLabel(status: string): string {
        const labelMap: { [key: string]: string } = {
            'pending': 'Procesando',
            'confirmed': 'Pedido Confirmado',
            'shipped': 'Enviado',
            'delivered': 'Entregado',
            'cancelled': 'Cancelado'
        };
        return labelMap[status] || status;
    }

    onViewDetails(): void {
        this.viewDetails.emit(this.order.idOrder);
    }

    formatDate(dateString: string): string {
        if (!dateString) return 'Fecha no disponible';
        const date = new Date(dateString);
        // Check if date is valid
        if (isNaN(date.getTime())) return 'Fecha inválida';

        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }
}
