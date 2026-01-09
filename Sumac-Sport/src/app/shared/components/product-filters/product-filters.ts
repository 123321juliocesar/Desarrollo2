import { Component, OnInit, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../../core/services/category';
import { BrandService } from '../../../core/services/brand';
import { Category } from '../../../core/models/category.model';
import { Brand } from '../../../core/models/brand.model';
import { ProductFilters } from '../../../core/services/filter.service';

@Component({
    selector: 'app-product-filters',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './product-filters.html',
    styleUrl: './product-filters.css',
})
export class ProductFiltersComponent implements OnInit {
    @Output() filtersChange = new EventEmitter<ProductFilters>();

    categories: Category[] = [];
    brands: Brand[] = [];

    // Available sizes and colors (will be populated from products)
    availableSizes: string[] = ['XS', 'S', 'M', 'L', 'XL', 'XXL'];
    availableColors: string[] = ['Negro', 'Blanco', 'Azul', 'Rojo', 'Verde', 'Gris'];

    // Selected filters
    selectedCategories: Set<string> = new Set();
    selectedBrands: Set<string> = new Set();
    selectedSizes: Set<string> = new Set();
    selectedColors: Set<string> = new Set();
    minPrice: number | null = null;
    maxPrice: number | null = null;

    // Expandable sections
    categoriesExpanded = true;
    brandsExpanded = true;
    priceExpanded = true;
    sizesExpanded = true;
    colorsExpanded = true;

    constructor(
        private categoryService: CategoryService,
        private brandService: BrandService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadCategories();
        this.loadBrands();
    }

    loadCategories(): void {
        this.categoryService.obtenerCategorias().subscribe({
            next: (response) => {
                if (response.type === 'success') {
                    this.categories = response.categories ?? [];
                    this.cdr.markForCheck();
                }
            },
            error: (err) => console.error('Error loading categories:', err)
        });
    }

    loadBrands(): void {
        this.brandService.obtenerMarcas().subscribe({
            next: (response) => {
                if (response.type === 'success') {
                    this.brands = response.brands ?? [];
                    this.cdr.markForCheck();
                }
            },
            error: (err) => console.error('Error loading brands:', err)
        });
    }

    toggleCategory(categoryId: string): void {
        if (this.selectedCategories.has(categoryId)) {
            this.selectedCategories.delete(categoryId);
        } else {
            this.selectedCategories.add(categoryId);
        }
        this.emitFilters();
    }

    toggleBrand(brandId: string): void {
        if (this.selectedBrands.has(brandId)) {
            this.selectedBrands.delete(brandId);
        } else {
            this.selectedBrands.add(brandId);
        }
        this.emitFilters();
    }

    toggleSize(size: string): void {
        if (this.selectedSizes.has(size)) {
            this.selectedSizes.delete(size);
        } else {
            this.selectedSizes.add(size);
        }
        this.emitFilters();
    }

    toggleColor(color: string): void {
        if (this.selectedColors.has(color)) {
            this.selectedColors.delete(color);
        } else {
            this.selectedColors.add(color);
        }
        this.emitFilters();
    }

    onPriceChange(): void {
        this.emitFilters();
    }

    clearFilters(): void {
        this.selectedCategories.clear();
        this.selectedBrands.clear();
        this.selectedSizes.clear();
        this.selectedColors.clear();
        this.minPrice = null;
        this.maxPrice = null;
        this.emitFilters();
    }

    private emitFilters(): void {
        const filters: ProductFilters = {};

        // Only include first selected category (backend expects single category)
        if (this.selectedCategories.size > 0) {
            filters.idCategory = Array.from(this.selectedCategories)[0];
        }

        // Only include first selected brand (backend expects single brand)
        if (this.selectedBrands.size > 0) {
            filters.idBrand = Array.from(this.selectedBrands)[0];
        }

        if (this.selectedSizes.size > 0) {
            filters.sizes = Array.from(this.selectedSizes);
        }

        if (this.selectedColors.size > 0) {
            filters.colors = Array.from(this.selectedColors);
        }

        if (this.minPrice !== null && this.minPrice > 0) {
            filters.minPrice = this.minPrice;
        }

        if (this.maxPrice !== null && this.maxPrice > 0) {
            filters.maxPrice = this.maxPrice;
        }

        this.filtersChange.emit(filters);
    }
}
