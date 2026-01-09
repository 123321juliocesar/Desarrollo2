import { Component, OnInit, ChangeDetectorRef, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ProductoService } from '../../core/services/producto';
import { BrandService } from '../../core/services/brand';
import { CategoryService } from '../../core/services/category';
import { Product } from '../../core/models/product.model';
import { ProductFiltersComponent } from '../../shared/components/product-filters/product-filters';
import { FilterService, ProductFilters } from '../../core/services/filter.service';
import { ProductDetailModalService } from '../../core/services/product-detail-modal.service';
import { Subscription, combineLatest, forkJoin, map } from 'rxjs';
import { ProductCardComponent } from '../../shared/components/product-card/product-card';

@Component({
  selector: 'app-categoria',
  standalone: true,
  imports: [CommonModule, ProductCardComponent, ProductFiltersComponent],
  templateUrl: './categoria.html',
  styleUrl: './categoria.css',
})
export class Categoria implements OnInit, OnDestroy {

  listarProductos: any[] = [];
  currentFilters: ProductFilters = {};
  currentSearchTerm: string = '';

  private categoriesMap = new Map<string, string>();
  private brandsMap = new Map<string, string>();
  private initialDataLoaded = false;

  private subscriptions = new Subscription();

  constructor(
    private productoService: ProductoService,
    private categoryService: CategoryService,
    private brandService: BrandService,
    private filterService: FilterService,
    private modalService: ProductDetailModalService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  ngOnInit() {
    // Load initial metadata once
    this.loadInitialData().subscribe({
      next: () => {
        this.initialDataLoaded = true;
        this.loadProducts();
      },
      error: () => {
        // Even if metadata fails, try to load products
        this.loadProducts();
      }
    });

    // Subscribe to search term changes from navbar
    this.subscriptions.add(
      this.filterService.searchTerm$.subscribe(searchTerm => {
        this.currentSearchTerm = searchTerm;
        this.loadProducts();
      })
    );
  }

  loadInitialData() {
    return forkJoin({
      categories: this.categoryService.obtenerCategorias(),
      brands: this.brandService.obtenerMarcas()
    }).pipe(
      map(data => {
        if (data.categories.type === 'success' && data.categories.categories) {
          data.categories.categories.forEach(c => this.categoriesMap.set(c.idCategory, c.name));
        }
        if (data.brands.type === 'success' && data.brands.brands) {
          data.brands.brands.forEach(b => this.brandsMap.set(b.idBrand, b.name));
        }
        return true;
      })
    );
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  onFiltersChange(filters: ProductFilters): void {
    this.currentFilters = filters;
    this.loadProducts();
  }

  loadProducts(): void {
    const combinedFilters: any = {
      ...this.currentFilters
    };

    // Add search term if present
    if (this.currentSearchTerm && this.currentSearchTerm.trim()) {
      combinedFilters.search = this.currentSearchTerm.trim();
    }

    // Convert arrays to comma-separated strings for backend
    if (combinedFilters.sizes && Array.isArray(combinedFilters.sizes)) {
      combinedFilters.sizes = combinedFilters.sizes.join(',');
    }
    if (combinedFilters.colors && Array.isArray(combinedFilters.colors)) {
      combinedFilters.colors = combinedFilters.colors.join(',');
    }

    this.productoService.obtenerProductos(combinedFilters).subscribe({
      next: (response) => {
        if (response.type === 'success' && response.products) {
          this.listarProductos = response.products.map(product => ({
            ...product,
            categoryName: this.categoriesMap.get(product.idCategory) || 'General',
            brandName: this.brandsMap.get(product.idBrand) || 'Marca Propia'
          }));
          this.cdr.markForCheck();
        } else {
          console.error('Error fetching products:', response.listMessage);
        }
      },
      error: err => console.error('Error al obtener productos (Observable)', err)
    });
  }

  onProductClick(producto: Product) {
    console.log('Producto seleccionado:', producto);
  }

  onAddToCart(producto: Product) {
    console.log('Abriendo modal para:', producto);
    this.modalService.open(producto.idProduct);
  }
}
