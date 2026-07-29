import {Routes} from '@angular/router';
import {userDetailsResolver} from './user-details/user-details.resolver';

export const userRoutes: Routes =[
  {
    path:'',
    children:[
      {
        path: '',
        loadComponent: () =>
          import('./search-users/search-users.component').then((m)=>m.SearchUsersComponent),
      },
      {
        path: ':id/details',
        loadComponent: () =>
          import('./user-details/user-details.component').then((m)=>m.UserDetailsComponent),
        resolve: { user: userDetailsResolver },
      },
    ]
  },
];
