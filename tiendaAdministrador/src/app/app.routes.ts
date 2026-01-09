import { Routes } from '@angular/router';
import { authRoutes } from './auth/auth.routes';
import { authGuard } from './auth/guards/auth-guard';
export const routes: Routes = [

  {
    path: '',
    redirectTo: 'auth',
    pathMatch: 'full'
  },

  {
    path: 'auth',
    children: authRoutes
  },

  {

    path: '',
    loadComponent: () =>
      import('./layout/layout').then(m => m.Layout),
    canActivate: [authGuard],
    children: [
      {
        path: 'home',
        loadComponent: () =>
          import('./features/home/home').then(m => m.Home)
      },
      {
        path: 'producto',
        loadComponent: () =>
          import('./features/producto/producto').then(m => m.Producto)
      },
      {
        path: 'producto/crear',
        loadComponent: () =>
          import('./features/producto/crear-producto/crear-producto').then(m => m.CrearProducto)
      },
      {
        path: 'categoria/crear',
        loadComponent: () =>
          import('./features/producto/crear-categoria/crear-categoria').then(m => m.CrearCategoria)
      },
      {
        path: 'marca/crear',
        loadComponent: () =>
          import('./features/producto/crear-marca/crear-marca').then(m => m.CrearMarca)
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./features/admin-orders/admin-orders.component').then(m => m.AdminOrdersComponent)

      }

    ]

  }
];
