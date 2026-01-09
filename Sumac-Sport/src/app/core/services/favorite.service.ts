import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { Observable, throwError, of, BehaviorSubject } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { FavoriteResponse, FavoriteCheckResponse, FavoriteListResponse } from '../models/api-response.model';
import { loginService } from './loginService';

@Injectable({
    providedIn: 'root',
})
export class FavoriteService {
    private apiURL = 'http://localhost:8080/favorite';

    // Cache for favorite IDs
    private favoriteIdsSubject = new BehaviorSubject<Set<string>>(new Set());
    public favoriteIds$ = this.favoriteIdsSubject.asObservable();
    private areFavoritesLoaded = false;

    constructor(
        private http: HttpClient,
        private authService: loginService,
        @Inject(PLATFORM_ID) private platformId: Object
    ) {
        // Clear favorites on logout
        this.authService.currentUser.subscribe(user => {
            if (!user) {
                this.favoriteIdsSubject.next(new Set());
                this.areFavoritesLoaded = false;
            } else {
                // Load favorites when user logs in (or app starts with user logged in)
                if (isPlatformBrowser(this.platformId)) {
                    this.loadFavoriteIds();
                }
            }
        });
    }

    private getUserId(): string | null {
        return this.authService.getUserId();
    }

    // Load all favorite IDs for the current user
    loadFavoriteIds(): void {
        const idUser = this.getUserId();
        if (!idUser) return;

        const params = new HttpParams().set('idUser', idUser);
        this.http.get<string[]>(`${this.apiURL}/ids`, { params }).subscribe({
            next: (ids) => {
                this.favoriteIdsSubject.next(new Set(ids));
                this.areFavoritesLoaded = true;
            },
            error: (err) => console.error('Error loading favorite IDs', err)
        });
    }

    // Check if a product is favorite (synchronous check against cache)
    isFavorite(idProduct: string): boolean {
        return this.favoriteIdsSubject.value.has(idProduct);
    }

    // Reactive check
    isFavorite$(idProduct: string): Observable<boolean> {
        return this.favoriteIds$.pipe(
            map(ids => ids.has(idProduct))
        );
    }

    //Toggle favorite
    toggleFavorite(idProduct: string): Observable<FavoriteResponse> {
        const idUser = this.getUserId();

        if (!idUser) {
            console.error('Usuario no autenticado');
            return throwError(() => new Error('Usuario no autenticado'));
        }

        // Optimistic update
        const currentIds = new Set(this.favoriteIdsSubject.value);
        let action = '';

        if (currentIds.has(idProduct)) {
            currentIds.delete(idProduct);
            action = 'removed';
        } else {
            currentIds.add(idProduct);
            action = 'added';
        }

        this.favoriteIdsSubject.next(currentIds);

        const body = {
            dto: {
                favorite: {
                    idProduct: idProduct
                }
            }
        };

        const params = new HttpParams().set('idUser', idUser);

        return this.http.post<FavoriteResponse>(`${this.apiURL}/toggle`, body, { params }).pipe(
            tap(response => {
                if (response.type !== 'success') {
                    // Revert if error (optional, but good for consistency)
                    // For now, assume success or reload on error
                }
            }),
            catchError(err => {
                // Revert optimistic update on error
                // Reload to be safe
                this.loadFavoriteIds();
                return throwError(() => err);
            })
        );
    }

    //Verifica si un producto es favorito (Deprecated for batch usage, keeping for compatibility if needed)
    checkFavorite(idProduct: string): Observable<FavoriteCheckResponse> {
        // If we have loaded favorites, use cache
        if (this.areFavoritesLoaded) {
            const isFav = this.isFavorite(idProduct);
            return of({
                type: 'success',
                listMessage: [],
                favorite: isFav
            } as FavoriteCheckResponse);
        }

        // Fallback to API if really needed, but better to load batch
        const idUser = this.getUserId();

        if (!idUser) {
            return of({
                type: 'success',
                listMessage: [],
                favorite: false
            } as FavoriteCheckResponse);
        }

        const params = new HttpParams().set('idUser', idUser);
        return this.http.get<FavoriteCheckResponse>(`${this.apiURL}/check/${idProduct}`, { params }).pipe(
            tap(response => {
                if (response.type === 'success') {
                    // Update cache individually
                    const currentIds = new Set(this.favoriteIdsSubject.value);
                    if (response.favorite) currentIds.add(idProduct);
                    else currentIds.delete(idProduct);
                    this.favoriteIdsSubject.next(currentIds);
                }
            })
        );
    }

    //Obtiene la lista de favoritos
    getFavorites(): Observable<FavoriteListResponse> {
        const idUser = this.getUserId();

        if (!idUser) {
            return of({
                type: 'success',
                listMessage: [],
                favorites: []
            } as FavoriteListResponse);
        }

        const params = new HttpParams().set('idUser', idUser);
        return this.http.get<FavoriteListResponse>(this.apiURL, { params });
    }
}
