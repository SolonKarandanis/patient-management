import {Routes} from '@angular/router';

export const userRoutes: Routes =[
  {
    path:'',
    children:[
      {
        path: '',
        loadComponent: () =>
          import('./search-users/search-users.component').then((m)=>m.SearchUsersComponent),
      }
    ]
  },
];
