import { AllowedRolesDirective } from './allowed-roles.directive';
import {AuthService} from '@core/services/auth.service';
import {TemplateRef, ViewContainerRef} from '@angular/core';
import {TestBed} from '@angular/core/testing';

xdescribe('AllowedRolesDirective', () => {
  let directive: AllowedRolesDirective;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let view: jasmine.SpyObj<ViewContainerRef>;
  let template: jasmine.SpyObj<TemplateRef<any>>;

  beforeEach(() =>{
    authServiceSpy= jasmine.createSpyObj('AuthService',[
      'loggedInUserRoleIds',
    ]);

    TestBed.configureTestingModule({
      providers:[
        {
          provide: AuthService,
          useValue: authServiceSpy,
        },
      ]
    });

    directive = TestBed.inject(AllowedRolesDirective);
  });

  it('should be created', () => {
    expect(directive).toBeTruthy();
  });
});
