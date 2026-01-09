import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminOrderService } from '../../core/services/admin-order.service';

@Component({
    selector: 'app-admin-orders',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admin-orders.component.html',
    styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent implements OnInit {
    orders: any[] = [];
    filteredOrders: any[] = [];
    loading: boolean = true;
    selectedStatus: string = 'all';
    searchTerm: string = '';

    statusOptions = [
        { value: 'all', label: 'Todos' },
        { value: 'pending', label: 'Procesando' },
        { value: 'confirmed', label: 'Confirmados' },
        { value: 'shipped', label: 'Enviados' },
        { value: 'delivered', label: 'Entregados' },
        { value: 'cancelled', label: 'Cancelados' }
    ];

    constructor(private adminOrderService: AdminOrderService) { }

    ngOnInit(): void {
        this.loadOrders();
    }

    loadOrders(): void {
        this.loading = true;

        const request = this.selectedStatus === 'all'
            ? this.adminOrderService.getAllOrders()
            : this.adminOrderService.getOrdersByStatus(this.selectedStatus);

        request.subscribe({
            next: (data) => {
                this.orders = data;
                this.filterOrders(); // Apply filter initially
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading orders:', err);
                this.loading = false;
            }
        });
    }

    filterOrders(): void {
        let temp = this.orders;

        if (this.selectedStatus !== 'all') {
            temp = temp.filter(o => o.status === this.selectedStatus);
        }

        if (this.searchTerm) {
            const term = this.searchTerm.toLowerCase();
            temp = temp.filter(o =>
                (o.orderNumber && o.orderNumber.toLowerCase().includes(term)) ||
                (o.userName && o.userName.toLowerCase().includes(term)) ||
                (o.idUser && o.idUser.toLowerCase().includes(term))
            );
        }

        this.filteredOrders = temp;
    }

    updateStatus(orderId: string, newStatus: string): void {
        if (confirm(`¿Cambiar estado a "${this.getStatusLabel(newStatus)}"?`)) {
            this.adminOrderService.updateOrderStatus(orderId, newStatus).subscribe({
                next: (updatedOrder) => {
                    // Update main list
                    const index = this.orders.findIndex(o => o.idOrder === orderId);
                    if (index !== -1) {
                        this.orders[index] = updatedOrder;
                    }

                    // Update filtered list (which is what is displayed) directly if possible, or re-filter
                    // If we are filtering by a specific status, we might need to remove it from view
                    if (this.selectedStatus !== 'all' && this.selectedStatus !== newStatus) {
                        // Remove from view if it no longer matches the filter
                        this.filteredOrders = this.filteredOrders.filter(o => o.idOrder !== orderId);
                    } else {
                        // Just update the object in the filtered list
                        const matchIndex = this.filteredOrders.findIndex(o => o.idOrder === orderId);
                        if (matchIndex !== -1) {
                            this.filteredOrders[matchIndex] = updatedOrder;
                        } else {
                            // Should ideally re-run filter logic to be safe
                            this.filterOrders();
                        }
                    }

                    alert('Estado actualizado correctamente');
                },
                error: (err) => {
                    console.error('Error updating status:', err);
                    alert('Error al actualizar el estado: ' + (err.error?.error || 'Error desconocido'));
                }
            });
        }
    }

    onStatusFilterChange(): void {
        this.loadOrders();
    }

    onSearchChange(): void {
        this.filterOrders();
    }

    getStatusLabel(status: string): string {
        const labels: { [key: string]: string } = {
            'pending': 'Procesando',
            'confirmed': 'Confirmado',
            'shipped': 'Enviado',
            'delivered': 'Entregado',
            'cancelled': 'Cancelado'
        };
        return labels[status] || status;
    }
}
