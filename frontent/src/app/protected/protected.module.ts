import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ProtectedComponent} from './protected.component';
import {RouterModule} from '@angular/router';
import {protectedRoutes} from './protected.routes';
import {SidebarComponent} from '../components/sidebar/sidebar.component';
import {FooterSmallComponent} from '../components/footer-small/footer-small.component';
import {HeaderComponent} from '../components/header/header.component';
import {FooterComponent} from '../components/footer/footer.component';
import {LoaderComponent} from '@components/loader/loader.component';



@NgModule({
  declarations: [ProtectedComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(protectedRoutes),
    SidebarComponent,
    FooterSmallComponent,
    HeaderComponent,
    FooterComponent,
    LoaderComponent,
  ],
  exports: [RouterModule],
})
export class ProtectedModule { }
