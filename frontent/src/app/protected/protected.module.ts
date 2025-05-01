import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ProtectedComponent} from './protected.component';
import {RouterModule} from '@angular/router';
import {protectedRoutes} from './protected.routes';



@NgModule({
  declarations: [ProtectedComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(protectedRoutes),
  ],
  exports: [RouterModule],
})
export class ProtectedModule { }
