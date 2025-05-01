import {Routes} from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';

export const protectedRoutes: Routes =[
  {
    path: 'dashboard',
    component:DashboardComponent
  },
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full',
  },
];
