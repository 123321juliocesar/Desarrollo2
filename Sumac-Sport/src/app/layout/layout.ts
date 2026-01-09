import { Component, OnInit, HostListener } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { Navbar } from './navbar/navbar';
import { FilterService } from '../core/services/filter.service';
import { CartService } from '../core/services/cart.service';
import { loginService } from '../core/services/loginService';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ProductDetail } from '../features/product-detail/product-detail';
import { ProductDetailModalService } from '../core/services/product-detail-modal.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, Navbar, CommonModule, ProductDetail],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout implements OnInit {

  searchTerm = '';
  cartCount$: Observable<number>;

  constructor(
    public router: Router,
    private filterService: FilterService,
    public authService: loginService,
    private cartService: CartService,
    private modalService: ProductDetailModalService
  ) {
    this.cartCount$ = this.cartService.cartCount$;
    this.isModalOpen$ = this.modalService.isOpen$;
    this.modalProductId$ = this.modalService.productId$;
  }

  isModalOpen$: Observable<boolean>;
  modalProductId$: Observable<string | null>;
  isUserDropdownOpen: boolean = false;

  closeModal() {
    this.modalService.close();
  }

  ngOnInit() {
    // Initialization if needed
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    // Close dropdown when clicking outside
    this.isUserDropdownOpen = false;
  }

  toggleUserDropdown(event: Event) {
    event.stopPropagation();
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }

  navigateToProfile(event: Event) {
    event.stopPropagation();
    this.isUserDropdownOpen = false;
    this.router.navigate(['/editar-register']);
  }

  logout(event: Event) {
    event.stopPropagation();
    this.isUserDropdownOpen = false;
    this.authService.logout();
    this.router.navigate(['/home']);
  }

  onSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm = input.value;
    this.filterService.setSearchTerm(this.searchTerm);
  }

  onSearchKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      const input = event.target as HTMLInputElement;
      this.searchTerm = input.value;
      this.filterService.setSearchTerm(this.searchTerm);
    }
  }
}
