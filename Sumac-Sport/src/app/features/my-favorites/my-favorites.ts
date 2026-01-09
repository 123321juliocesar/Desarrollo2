import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FavoriteService } from '../../core/services/favorite.service';
import { DtoFavoriteWithProduct } from '../../core/models/api-response.model';
import { ProductCardComponent } from '../../shared/components/product-card/product-card';

@Component({
    selector: 'app-my-favorites',
    standalone: true,
    imports: [CommonModule, ProductCardComponent],
    templateUrl: './my-favorites.html',
    styleUrl: './my-favorites.css'
})
export class MyFavorites implements OnInit {
    favorites: DtoFavoriteWithProduct[] = [];
    loading: boolean = true;
    error: string | null = null;

    constructor(
        private favoriteService: FavoriteService,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadFavorites();
    }

    loadFavorites(): void {
        this.loading = true;
        this.favoriteService.getFavorites().subscribe({
            next: (response) => {
                if (response.type === 'success') {
                    this.favorites = response.favorites;
                } else {
                    this.error = 'Error al cargar favoritos';
                }
                this.loading = false;
                this.cdr.markForCheck();
            },
            error: (err) => {
                console.error('Error loading favorites', err);
                this.error = 'No se pudieron cargar tus favoritos';
                this.loading = false;
                this.cdr.markForCheck();
            }
        });
    }

    onProductClick(product: any): void {
        this.router.navigate(['/producto', product.idProduct]);
    }
}
