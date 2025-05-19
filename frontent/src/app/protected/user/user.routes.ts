import {Routes} from '@angular/router';

export const userRoutes: Routes =[
  {
    path:'',
    children:[
      {
        path: 'search',
        children:[
          {
            path: '',
            loadComponent: () =>
              import('./search-users/search-users.component').then((m)=>m.SearchUsersComponent),
          }
        ]
      }
    ]
  },
  { path: '', redirectTo: 'search' ,pathMatch: 'full',},
];
