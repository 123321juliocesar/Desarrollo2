import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../core/models/order.model';
import { EmptyOrdersComponent } from './components/empty-orders/empty-orders.component';
import { OrderCardComponent } from './components/order-card/order-card.component';

@Component({
  selector: 'app-pedidos-cuenta',
  standalone: true,
  imports: [CommonModule, EmptyOrdersComponent, OrderCardComponent],
  templateUrl: './pedidos-cuenta.html',
  styleUrl: './pedidos-cuenta.css',
})
export class PedidosCuenta implements OnInit {
  orders: Order[] = [];
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private orderService: OrderService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      console.log('PedidosCuenta initiated');
      this.loadOrders();
    }
  }

  loadOrders(): void {
    this.loading = true;
    this.error = null;

    console.log('Loading orders...');

    try {
      this.orderService.getMyOrders().subscribe({
        next: (response) => {
          console.log('Orders loaded successfully:', response);
          this.orders = response;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading orders:', err);
          this.error = 'No se pudieron cargar los pedidos';
          this.loading = false;
        }
      });
    } catch (err) {
      console.error('Exception loading orders:', err);
      this.error = 'Error al intentar cargar los pedidos';
      this.loading = false;
    }
  }

  onViewOrderDetails(orderId: string): void {
    this.router.navigate(['/my-orders', orderId]);
  }
}

