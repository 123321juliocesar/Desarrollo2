import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';

@Component({
    selector: 'app-order-detail',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './order-detail.component.html',
    styleUrl: './order-detail.component.css',
})
export class OrderDetailComponent implements OnInit {
    order: Order | null = null;
    loading: boolean = true;
    error: string | null = null;

    statusSteps = [
        { key: 'confirmed', label: 'Confirmado', icon: '✓' },
        { key: 'shipped', label: 'Enviado', icon: '📦' },
        { key: 'delivered', label: 'Entregado', icon: '✓' }
    ];

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private orderService: OrderService,
        @Inject(PLATFORM_ID) private platformId: Object
    ) { }

    ngOnInit(): void {
        if (isPlatformBrowser(this.platformId)) {
            this.route.paramMap.subscribe(params => {
                const orderId = params.get('id');
                if (orderId) {
                    this.loadOrderDetail(orderId);
                } else {
                    this.router.navigate(['/my-orders']);
                }
            });
        }
    }

    loadOrderDetail(orderId: string): void {
        this.loading = true;
        this.error = null;

        this.orderService.getOrderById(orderId).subscribe({
            next: (response) => {
                this.order = response;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading order detail:', err);
                this.error = 'No se pudo cargar el detalle del pedido';
                this.loading = false;
            }
        });
    }

    getStatusIndex(status: string): number {
        const statusMap: { [key: string]: number } = {
            'pending': 0,
            'confirmed': 0,
            'shipped': 1,
            'delivered': 2,
            'cancelled': -1
        };
        return statusMap[status] ?? 0;
    }

    isStepActive(stepIndex: number): boolean {
        if (!this.order) return false;
        const currentIndex = this.getStatusIndex(this.order.status);
        return stepIndex <= currentIndex;
    }

    formatDate(dateString: string): string {
        if (!dateString) return 'Fecha no disponible';
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return 'Fecha inválida';

        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    goBack(): void {
        this.router.navigate(['/my-orders']);
    }
}
