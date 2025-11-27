import {Routes} from '@angular/router';

export const resourceRoutes: Routes =[
  {
    path:'',
    loadComponent: () =>
      import('./i18n-management/i18n-management.component').then((m)=>m.I18nManagementComponent),
  }
];
