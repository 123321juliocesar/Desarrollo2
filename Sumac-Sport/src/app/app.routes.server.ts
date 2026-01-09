import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [

  {
    path: 'id/:id',
    renderMode: RenderMode.Client
  },
  {
    path: 'producto/:id',
    renderMode: RenderMode.Client
  },
  {
    path: 'my-orders/:id',
    renderMode: RenderMode.Client
  },


  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];

