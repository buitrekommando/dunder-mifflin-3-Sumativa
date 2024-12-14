import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import JefeResolve from './route/jefe-routing-resolve.service';

const jefeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/jefe.component').then(m => m.JefeComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/jefe-detail.component').then(m => m.JefeDetailComponent),
    resolve: {
      jefe: JefeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/jefe-update.component').then(m => m.JefeUpdateComponent),
    resolve: {
      jefe: JefeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/jefe-update.component').then(m => m.JefeUpdateComponent),
    resolve: {
      jefe: JefeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default jefeRoute;
