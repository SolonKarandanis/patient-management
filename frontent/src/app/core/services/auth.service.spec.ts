import { TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { AuthStore } from "../store/auth/auth.store";
import {AuthService} from './auth.service';
import {mockLoginCredentials} from '../../../../testing/mockData';
import {RolesConstants} from '../guards/SecurityConstants';
import {signal} from '@angular/core';

type AuthStore = InstanceType<typeof AuthStore>;

describe('AuthService', () =>{
  let service: AuthService;
  let authStoreSpy: jasmine.SpyObj<AuthStore>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(()=>{
    authStoreSpy = jasmine.createSpyObj('AuthStore',[
      'isLoading',
      'isLoggedIn',
      'loggedUser',
      'isJwtExpired',
      'login',
      'logout',
      'getUserAccount',
      'hasAnyAuthority',
      'getUsername',
    ]);

    routerSpy = jasmine.createSpyObj('Router',['navigate']);

    TestBed.configureTestingModule({
      providers:[
        {
          provide: AuthStore,
          useValue: authStoreSpy,
        },
        {
          provide: Router,
          useValue: routerSpy,
        },
      ]
    });

    service = TestBed.inject(AuthService);
  })

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should perform login ', () =>{
    service.login(mockLoginCredentials);

    expect(authStoreSpy.login).toHaveBeenCalledWith(mockLoginCredentials);
    expect(authStoreSpy.login).toHaveBeenCalledTimes(1);
  });

  it('should perform logout ', () =>{
    service.logout();

    expect(authStoreSpy.logout).toHaveBeenCalled();
    expect(authStoreSpy.logout).toHaveBeenCalledTimes(1);
  });

  it('should get user account by token ', () =>{
    service.getUserByToken();

    expect(authStoreSpy.getUserAccount).toHaveBeenCalled();
    expect(authStoreSpy.getUserAccount).toHaveBeenCalledTimes(1);
  });

  it('should check if user has any authority', () =>{
    service.hasAnyAuthority(RolesConstants.ROLE_DOCTOR);

    expect(authStoreSpy.hasAnyAuthority).toHaveBeenCalledWith(RolesConstants.ROLE_DOCTOR);
    expect(authStoreSpy.hasAnyAuthority).toHaveBeenCalledTimes(1);
  });

  it('should check if user is authenticated (true)', () =>{
    const isLoggedIn = signal(true);
    service.isLoggedIn=isLoggedIn;
    authStoreSpy.isJwtExpired.and.returnValue(false)

    service.isAuthenticated();

    expect(authStoreSpy.isJwtExpired).toHaveBeenCalled();
    expect(authStoreSpy.isJwtExpired).toHaveBeenCalledTimes(1);
  });

  it('should check if user is authenticated (false)', () =>{
    const isLoggedIn = signal(false);
    service.isLoggedIn=isLoggedIn;
    authStoreSpy.isJwtExpired.and.returnValue(true)

    service.isAuthenticated();

    expect(authStoreSpy.isJwtExpired).toHaveBeenCalledTimes(0);
  });

  it('should return the logged in users username', () =>{
    authStoreSpy.getUsername.and.returnValue("skaran");

    service.getUsername();

    expect(authStoreSpy.getUsername).toHaveBeenCalled();
    expect(authStoreSpy.getUsername).toHaveBeenCalledTimes(1);
  });
})
