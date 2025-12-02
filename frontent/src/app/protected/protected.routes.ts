import {Routes} from '@angular/router';
import {ProtectedComponent} from './protected.component';
import {Permissions} from '@models/permissions.model';
import {manageI18nResourcesEnabledGuard} from './i18n/guards/management-i18n-resource-enabled.guard';
import {ngxPermissionsGuard} from 'ngx-permissions';

export const protectedRoutes: Routes =[
  {
    path: '',
    component:ProtectedComponent,
    children:[
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path:'dashboard',
        loadComponent: () =>
          import('./dashboard/dashboard.component').then((m)=>m.DashboardComponent),
      },
      {
        path: 'users',
        loadChildren: () =>
          import('./user/user.module').then((m)=>m.UserModule),
      },
      {
        path: 'i18n-resource',
        loadChildren: () =>
          import('./i18n/i18n.module').then((m)=>m.I18nModule),
        canActivate: [ngxPermissionsGuard,manageI18nResourcesEnabledGuard],
        data: {
          permissions: {
            only: [Permissions.ManageResourceBundles],
          },
        },
      }
    ]
  },
];
