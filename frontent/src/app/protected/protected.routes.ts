import {Routes} from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';
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
      }
    ]
  },
];
