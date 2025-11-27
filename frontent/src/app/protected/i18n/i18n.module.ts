import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {resourceRoutes} from './i18n-resource.routes';

@NgModule({
  imports:[
    CommonModule,
    RouterModule.forChild(resourceRoutes),
  ],
})
export class I18nModule{ }
