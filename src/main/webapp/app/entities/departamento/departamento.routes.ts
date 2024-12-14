import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DepartamentoResolve from './route/departamento-routing-resolve.service';

const departamentoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/departamento.component').then(m => m.DepartamentoComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/departamento-detail.component').then(m => m.DepartamentoDetailComponent),
    resolve: {
      departamento: DepartamentoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/departamento-update.component').then(m => m.DepartamentoUpdateComponent),
    resolve: {
      departamento: DepartamentoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/departamento-update.component').then(m => m.DepartamentoUpdateComponent),
    resolve: {
      departamento: DepartamentoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default departamentoRoute;
