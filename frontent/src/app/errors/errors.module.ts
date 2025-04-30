import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {ErrorsComponent} from './errors.component';
import {Errors404Component} from './errors404/errors404.component';
import {Errors500Component} from './errors500/errors500.component';

const routes: Routes = [
  {
    path: '',
    component: ErrorsComponent,
    children: [
      {
        path: '404',
        component: Errors404Component,
      },
      {
        path: '500',
        component: Errors500Component,
      },
      { path: '', redirectTo: '404', pathMatch: 'full' },
      { path: '**', redirectTo: '404', pathMatch: 'full' },
    ],
  },
];

@NgModule({
  declarations: [ErrorsComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule],
})
export class ErrorsModule { }
