import { AllowedRolesDirective } from './allowed-roles.directive';
import {AuthService} from '@core/services/auth.service';
import {TemplateRef, ViewContainerRef} from '@angular/core';

xdescribe('AllowedRolesDirective', () => {
  let directive: AllowedRolesDirective;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let view: jasmine.SpyObj<ViewContainerRef>;
  let template: jasmine.SpyObj<TemplateRef<any>>;

  beforeEach(() =>{

  });
});
