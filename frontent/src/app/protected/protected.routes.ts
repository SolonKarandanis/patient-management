import {Routes} from '@angular/router';
import {ProtectedComponent} from './protected.component';

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
      }
    ]
  },
];
