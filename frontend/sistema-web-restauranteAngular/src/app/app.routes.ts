import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { authGuard } from './guards/auth-guard';
import { roleGuard } from './guards/role-guard';
import { AdminCategoriaComponent } from './components/admin/admin-categorias/admin-categorias';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  {
    path: 'admin',
    loadComponent: () => import('./components/admin/admin')
      .then(m => m.Admin),
    children: [
      {
        path: 'usuarios',
        loadComponent: () => import('./components/admin/admin-usuarios/admin-usuarios')
          .then(m => m.AdminUsuariosComponent)
      },
      {
        path: 'categorias',
        loadComponent: () => import('./components/admin/admin-categorias/admin-categorias')
          .then(m => m.AdminCategoriaComponent)
      },
      {
        path: 'mesas',
        loadComponent: () => import('./components/admin/admin-mesas/admin-mesas')
          .then(m => m.AdminMesasComponent)
      },
      {
        path: 'comidas',
        loadComponent: () => import('./components/admin/admin-comidas/admin-comidas')
          .then(m => m.AdminComidasComponent)
      }
    ]
  },

  {
    path: 'cocina',
    loadComponent: () => import('./components/cocina/cocina')
      .then(m => m.Cocina),
    children: [
      {
        path: 'pedidos',
        loadComponent: () =>
          import('./components/cocina/cocina-pedidos/cocina-pedidos')
          .then(m => m.CocinaPedidosComponent)
      },
      {
        path: 'comidas',
        loadComponent: () =>
          import('./components/cocina/cocina-comidas/cocina-comidas')
          .then(m => m.CocinaComidasComponent)
      }
    ]
  },
  {
    path: 'mesero',
    loadComponent: () => import('./components/mesero/mesero')
      .then(m => m.Mesero),
    canActivate: [authGuard, roleGuard(['ROLE_MESERO'])],

    children: [
      {
        path: 'pedidos',
        loadComponent: () => import('./components/mesero/mesero-crearPedido/mesero-crearPedido')
          .then(m => m.PedidoBuilderComponent)
      },
      {
        path: 'historial',
        loadComponent: () => import('./components/mesero/mesero-historial/mesero-historial')
          .then(m => m.MeseroHistorialComponent)
      }
    ]
  },
  {
    path: 'caja',
    loadComponent: () => import('./components/cajero/cajero')
      .then(m => m.Caja),
    canActivate: [authGuard, roleGuard(['ROLE_CAJERO'])],

        children: [
      {
        path: 'comprobante',
        loadComponent: () => import('./components/cajero/cajero-comprobante/cajero-generar-comprobante')
          .then(m => m.CajeroGenerarComprobanteComponent)
      },
      {
        path: 'historial',
        loadComponent: () => import('./components/cajero/cajero-historial/cajero-historial')
          .then(m => m.CajeroHistorialComprobantesComponent)
      }
      /*{
        path: 'reporte',
        loadComponent: () => import('./components/cajero/cajero-reporte/cajero-reporte')
          .then(m => m.CajeroGenerarComprobanteComponent)
      }*/
    ]
  },


  { path: '', redirectTo: 'login', pathMatch: 'full' }
];







