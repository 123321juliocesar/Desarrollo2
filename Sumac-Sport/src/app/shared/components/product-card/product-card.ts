import { Component, Input, Output, EventEmitter, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Product } from '../../../core/models/product.model';
import { FavoriteService } from '../../../core/services/favorite.service';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css'
})
export class ProductCardComponent implements OnInit {
  @Input() product: any;
  @Output() productClick = new EventEmitter<any>();
  @Output() addToCart = new EventEmitter<any>();

  isFavorite: boolean = false;
  isLoadingFavorite: boolean = false;

  constructor(
    private router: Router,
    private favoriteService: FavoriteService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    if (this.product) {
      // Subscribe to reactive favorite state
      this.favoriteService.isFavorite$(this.product.idProduct).subscribe(isFav => {
        this.isFavorite = isFav;
        this.cdr.markForCheck();
      });
    }
  }

  toggleFavorite(event: Event): void {
    event.stopPropagation();
    if (this.isLoadingFavorite) return;

    this.isLoadingFavorite = true;

    // Service handles optimistic update via isFavorite$ subscription
    this.favoriteService.toggleFavorite(this.product.idProduct).subscribe({
      next: (response) => {
        this.isLoadingFavorite = false;
        if (response.type !== 'success') {
          // Error handling is managed by service reversing the state
        }
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error toggling favorite', err);
        this.isLoadingFavorite = false;
        this.cdr.markForCheck();
      }
    });
  }

  onCardClick(): void {
    this.productClick.emit(this.product);
    this.router.navigate(['/producto', this.product.idProduct]);
  }

  onAddToCart(event: Event): void {
    event.stopPropagation();
    this.addToCart.emit(this.product);
  }

  hasDiscount(): boolean {
    return this.product.oldPrice !== null && this.product.oldPrice > this.product.price;
  }

  getDiscountPercentage(): number {
    if (!this.hasDiscount() || !this.product.oldPrice) return 0;
    return Math.round(((this.product.oldPrice - this.product.price) / this.product.oldPrice) * 100);
  }

  getStarArray(): number[] {
    return Array(5).fill(0).map((_, i) => i + 1);
  }

  isStarFilled(star: number): boolean {
    return star <= Math.round(this.product.averageRating);
  }
}
