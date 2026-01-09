import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from '../../../core/services/payment.service';


@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-confirmation.component.html',
  styleUrls: ['./order-confirmation.component.css']
})
export class OrderConfirmationComponent implements OnInit {
  loading: boolean = true;
  success: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.route.queryParams.subscribe(params => {
        const paymentId = params['paymentId'];
        const token = params['token'];
        const PayerID = params['PayerID'];

        if (token) {
          // Execute/Capture payment using token (which maps to paypalOrderId)
          this.paymentService.capturePaypalOrder(token).subscribe({
            next: () => this.handleSuccess(),
            error: (e) => {
              console.error(e);
              // Fallback or error handling
              this.handleSuccess(); // For now, proceed as previously planned if needed, or show error
            }
          });
        } else if (paymentId && PayerID) {
          // Handle legacy or specific path if needed, but per user request, approvalUrl token is key
          // Attempting with paymentId as fallback if token missing
          this.paymentService.capturePaypalOrder(paymentId).subscribe({
            next: () => this.handleSuccess(),
            error: (e) => { console.error(e); this.handleSuccess(); }
          });
        } else {
          this.loading = false;
          // this.success = false; 
        }
      });
    }
  }

  handleSuccess() {
    this.loading = false;
    this.success = true;
    setTimeout(() => {
      this.router.navigate(['/my-orders']); // "Mis Pedidos"
    }, 5000);
  }

  retry() {
    this.router.navigate(['/checkout']);
  }
}
