import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ProductDetailModalService {
    private isOpenSubject = new BehaviorSubject<boolean>(false);
    public isOpen$ = this.isOpenSubject.asObservable();

    private productIdSubject = new BehaviorSubject<string | null>(null);
    public productId$ = this.productIdSubject.asObservable();

    open(productId: string) {
        this.productIdSubject.next(productId);
        this.isOpenSubject.next(true);
    }

    close() {
        this.isOpenSubject.next(false);
        this.productIdSubject.next(null);
    }
}
