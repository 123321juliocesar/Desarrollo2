import { Component, OnInit, ChangeDetectorRef, Inject, PLATFORM_ID, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductoService } from '../../core/services/producto';
import { BrandService } from '../../core/services/brand';
import { FavoriteService } from '../../core/services/favorite.service';
import { CartService } from '../../core/services/cart.service';
import { loginService } from '../../core/services/loginService';
import { ProductDetailModalService } from '../../core/services/product-detail-modal.service';
import { ProductDetail as ProductData, ProductVariant } from '../../core/models/product.model';

@Component({
    selector: 'app-product-detail',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './product-detail.html',
    styleUrl: './product-detail.css',
})
export class ProductDetail implements OnInit {
    @Input() productId: string | null = null; // Input for modal mode
    @Input() isModal: boolean = false;        // Flag for modal mode
    @Output() closeModal = new EventEmitter<void>();

    producto: ProductData | null = null;
    selectedSize: string | null = null;
    selectedColor: string | null = null;
    selectedVariant: ProductVariant | null = null;
    quantity: number = 1;
    loading: boolean = true;
    error: string | null = null;

    // Obtener tallas y colores únicos
    get availableSizes(): string[] {
        if (!this.producto?.variants) return [];
        const sizes = this.producto.variants
            .map((v: ProductVariant) => v.size)
            .filter((size: string | null): size is string => size !== null && size !== undefined);
        return [...new Set(sizes)];
    }

    get availableColors(): { color: string; colorHex: string | null }[] {
        if (!this.producto?.variants) return [];
        const colorsMap = new Map<string, string | null>();

        this.producto.variants.forEach((v: ProductVariant) => {
            if (v.color) {
                colorsMap.set(v.color, v.colorHex);
            }
        });

        return Array.from(colorsMap.entries()).map(([color, colorHex]) => ({
            color,
            colorHex
        }));
    }

    brandName: string = '';

    isFavorite: boolean = false;
    isLoadingFavorite: boolean = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private productoService: ProductoService,
        private brandService: BrandService,
        private favoriteService: FavoriteService,
        private cartService: CartService,
        private authService: loginService,
        private modalService: ProductDetailModalService,
        private cdr: ChangeDetectorRef,
        @Inject(PLATFORM_ID) private platformId: Object
    ) { }

    ngOnInit() {
        // Decide whether to use Input ID (modal) or Route ID (page)
        const id = this.productId || this.route.snapshot.paramMap.get('id');

        if (id) {
            this.loadProduct(id);
        } else {
            this.error = 'ID de producto no válido';
            this.loading = false;
        }
    }

    loadProduct(id: string) {
        this.loading = true;
        this.productoService.obtenerProductoPorId(id).subscribe({
            next: (response) => {
                if (response.type === 'success' && response.product) {
                    const prod = response.product;
                    this.producto = prod;
                    this.loadBrand(prod.idBrand);

                    // Check favorite status
                    this.checkFavoriteStatus(prod.idProduct);

                    this.loading = false;
                    this.cdr.markForCheck();
                } else {
                    this.error = response.listMessage.join(', ') || 'Error al cargar el producto';
                    this.loading = false;
                    this.cdr.markForCheck();
                }
            },
            error: (err) => {
                console.error('Error al obtener producto:', err);
                this.error = 'Error al cargar el producto';
                this.loading = false;
                this.cdr.markForCheck();
            }
        });
    }

    checkFavoriteStatus(idProduct: string) {
        this.favoriteService.checkFavorite(idProduct).subscribe({
            next: (response) => {
                if (response.type === 'success') {
                    this.isFavorite = response.favorite;
                    this.cdr.markForCheck();
                }
            },
            error: (err) => console.error('Error checking favorite status', err)
        });
    }

    loadBrand(idBrand: string) {
        if (!idBrand) return;
        this.brandService.obtenerMarcaPorId(idBrand).subscribe({
            next: (response) => {
                if (response.type === 'success' && response.brand) {
                    this.brandName = response.brand.name;
                    this.cdr.markForCheck();
                }
            },
            error: (err) => console.error('Error al cargar marca:', err)
        });
    }

    selectSize(size: string) {
        this.selectedSize = size;
        this.updateSelectedVariant();
    }

    selectColor(color: string) {
        this.selectedColor = color;
        this.updateSelectedVariant();
    }

    updateSelectedVariant() {
        if (!this.producto?.variants) return;

        this.selectedVariant = this.producto.variants.find((v: ProductVariant) =>
            v.size === this.selectedSize && v.color === this.selectedColor
        ) || null;
    }

    isSizeAvailable(size: string): boolean {
        if (!this.producto?.variants) return false;

        // Si no hay color seleccionado, verificar si existe alguna variante con esta talla
        if (!this.selectedColor) {
            return this.producto.variants.some((v: ProductVariant) => v.size === size && v.stock > 0);
        }

        // Si hay color seleccionado, verificar si existe la combinación con stock
        return this.producto.variants.some((v: ProductVariant) =>
            v.size === size && v.color === this.selectedColor && v.stock > 0
        );
    }

    isColorAvailable(color: string): boolean {
        if (!this.producto?.variants) return false;

        // Si no hay talla seleccionada, verificar si existe alguna variante con este color
        if (!this.selectedSize) {
            return this.producto.variants.some((v: ProductVariant) => v.color === color && v.stock > 0);
        }

        // Si hay talla seleccionada, verificar si existe la combinación con stock
        return this.producto.variants.some((v: ProductVariant) =>
            v.color === color && v.size === this.selectedSize && v.stock > 0
        );
    }

    incrementQuantity() {
        if (this.selectedVariant && this.quantity < this.selectedVariant.stock) {
            this.quantity++;
        }
    }

    decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    addToCart() {
        if (!this.selectedSize || !this.selectedColor) {
            if (isPlatformBrowser(this.platformId)) {
                alert('Por favor selecciona una talla y un color');
            }
            return;
        }

        const currentProduct = this.producto;
        const currentVariant = this.selectedVariant;

        if (!currentVariant || currentVariant.stock === 0) {
            if (isPlatformBrowser(this.platformId)) {
                alert('Esta variante no está disponible');
            }
            return;
        }

        // Check if user is logged in
        if (!this.authService.isLoggedIn()) {
            this.router.navigate(['/login']);
            // Close modal if open
            if (this.isModal) this.closeModal.emit();
            return;
        }

        const userId = this.authService.getUserId();

        if (!userId || !currentProduct) return;

        // Call service
        const request = {
            idUser: userId,
            idProduct: currentProduct.idProduct,
            idVariant: currentVariant.idVariant,
            quantity: this.quantity
        };

        this.cartService.addToCart(request).subscribe({
            next: (cart) => {
                if (isPlatformBrowser(this.platformId)) {
                    alert(`Producto agregado al carrito exitosamente.`);
                }

                // If in modal mode, close it after success
                if (this.isModal) {
                    this.closeModal.emit();
                }
            },
            error: (err) => {
                console.error('Error adding to cart', err);
                if (isPlatformBrowser(this.platformId)) {
                    alert('Hubo un error al agregar el producto al carrito.');
                }
            }
        });
    }

    toggleFavorite() {
        if (!this.producto || this.isLoadingFavorite) return;

        this.isLoadingFavorite = true;

        // Optimistic UI update
        const previousState = this.isFavorite;
        this.isFavorite = !this.isFavorite;

        this.favoriteService.toggleFavorite(this.producto.idProduct).subscribe({
            next: (response) => {
                this.isLoadingFavorite = false;
                if (response.type === 'success') {
                    if (response.action === 'added') this.isFavorite = true;
                    else if (response.action === 'removed') this.isFavorite = false;
                    this.cdr.markForCheck();
                } else {
                    this.isFavorite = previousState;
                    this.cdr.markForCheck();
                }
            },
            error: (err) => {
                console.error('Error adding to favorites', err);
                this.isLoadingFavorite = false;
                this.isFavorite = previousState;
                this.cdr.markForCheck();
            }
        });
    }

    goBack() {
        if (this.isModal) {
            this.closeModal.emit();
        } else {
            if (isPlatformBrowser(this.platformId)) {
                this.router.navigate(['/home']);
            }
        }
    }

    getStarArray(): number[] {
        return [1, 2, 3, 4, 5];
    }
}
