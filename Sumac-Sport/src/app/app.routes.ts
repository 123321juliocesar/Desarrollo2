import { Routes, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Layout } from './layout/layout';
import { loginService } from './core/services/loginService';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  {
    path: '', component: Layout,
    children: [
      { path: 'home', loadComponent: () => import('./features/home/home').then(m => m.Home) },
      { path: 'id/:id', loadComponent: () => import('./features/product-detail/product-detail').then(m => m.ProductDetail) },
      { path: 'producto/:id', loadComponent: () => import('./features/product-detail/product-detail').then(m => m.ProductDetail) },
      { path: 'carrito', loadComponent: () => import('./features/cart/cart.component').then(m => m.CartComponent) },
      { path: 'login', loadComponent: () => import('./features/auth/login/login').then(m => m.Login) },
      { path: 'register', loadComponent: () => import('./features/register/register').then(m => m.Register) },
      { path: 'categoria', loadComponent: () => import('./features/categoria/categoria').then(m => m.Categoria) },
      { path: 'editar-register', loadComponent: () => import('./features/register/register').then(m => m.Register) },
      { path: 'favoritos', loadComponent: () => import('./features/my-favorites/my-favorites').then(m => m.MyFavorites) },
      { path: 'checkout', loadComponent: () => import('./features/checkout/checkout.component').then(m => m.CheckoutComponent) },
      { path: 'order-confirmation', loadComponent: () => import('./features/checkout/order-confirmation/order-confirmation.component').then(m => m.OrderConfirmationComponent) },
      {
        path: 'my-orders',
        loadComponent: () => import('./features/pedidos-cuenta/pedidos-cuenta').then(m => m.PedidosCuenta),
        canActivate: [authGuard]
      },
      {
        path: 'my-orders/:id',
        loadComponent: () => import('./features/pedidos-cuenta/order-detail/order-detail.component').then(m => m.OrderDetailComponent),
        canActivate: [authGuard]
      },
    ]
  }

];
