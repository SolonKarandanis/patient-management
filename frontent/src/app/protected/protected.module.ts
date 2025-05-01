import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ProtectedComponent} from './protected.component';



@NgModule({
  declarations: [ProtectedComponent],
  imports: [
    CommonModule
  ]
})
export class ProtectedModule { }
