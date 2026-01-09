import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface ProductFilters {
    search?: string;
    idCategory?: string;
    idBrand?: string;
    sizes?: string[];
    colors?: string[];
    minPrice?: number;
    maxPrice?: number;
    sortBy?: string;
}

@Injectable({
    providedIn: 'root'
})
export class FilterService {
    private searchTermSubject = new BehaviorSubject<string>('');
    private filtersSubject = new BehaviorSubject<ProductFilters>({});

    searchTerm$ = this.searchTermSubject.asObservable();
    filters$ = this.filtersSubject.asObservable();

    setSearchTerm(term: string): void {
        this.searchTermSubject.next(term);
    }

    setFilters(filters: ProductFilters): void {
        this.filtersSubject.next(filters);
    }

    clearFilters(): void {
        this.filtersSubject.next({});
        this.searchTermSubject.next('');
    }

    getCurrentFilters(): ProductFilters {
        return this.filtersSubject.value;
    }

    getCurrentSearchTerm(): string {
        return this.searchTermSubject.value;
    }
}
