import {fakeAsync, TestBed, tick} from "@angular/core/testing";
import { Router } from "@angular/router";
import { AuthStore } from "../store/auth/auth.store";

import {computed, signal} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {mockLoginCredentials, mockUser} from '@testing/mockData';
import {RolesConstants} from '@core/guards/SecurityConstants';

describe('AuthService', () =>{
  let service: AuthService;
  let authStoreSpy: any;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockIsLoggedIn = signal(false);
  const mockStatus = signal<'pending' | 'loading' | 'loaded' | 'error'>('pending');

  beforeEach(()=>{
    authStoreSpy = jasmine.createSpyObj('AuthStore',[
      'login',
      'logout',
      'hasAnyAuthority',
      'getUsername',
      'isJwtExpired',
      'initAuth'
    ]);

    // Mock signals
    authStoreSpy.isLoggedIn = mockIsLoggedIn.asReadonly();
    authStoreSpy.status = mockStatus.asReadonly();
    authStoreSpy.loading = computed(() => mockStatus() === 'loading');
    authStoreSpy.loaded = computed(() => mockStatus() === 'loaded');
    authStoreSpy.getUser = signal(undefined).asReadonly();
    authStoreSpy.getUserId = signal(undefined).asReadonly();
    authStoreSpy.getRoleIds = signal([]).asReadonly();

    routerSpy = jasmine.createSpyObj('Router',['navigate']);

    TestBed.configureTestingModule({
      providers:[
        AuthService,
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

  it('should call initAuth on creation', () => {
    expect(authStoreSpy.initAuth).toHaveBeenCalled();
  });


  it('should perform login ', () =>{
    service.login(mockLoginCredentials);

    expect(authStoreSpy.login).toHaveBeenCalledWith(mockLoginCredentials);
  });

  it('should perform logout ', () =>{
    service.logout();

    expect(authStoreSpy.logout).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/auth/login']);
  });

  it('should check if user has any authority', () =>{
    service.hasAnyAuthority(RolesConstants.ROLE_DOCTOR);

    expect(authStoreSpy.hasAnyAuthority).toHaveBeenCalledWith(RolesConstants.ROLE_DOCTOR);
  });

  describe('isAuthenticated', () => {
    it('should return true if user is logged in and token is not expired', () =>{
      mockIsLoggedIn.set(true);
      authStoreSpy.isJwtExpired.and.returnValue(false)

      const result = service.isAuthenticated();

      expect(result).toBe(true);
      expect(authStoreSpy.isJwtExpired).toHaveBeenCalled();
    });

    it('should return false if user is not logged in', () =>{
      mockIsLoggedIn.set(false);
      authStoreSpy.isJwtExpired.and.returnValue(false)

      const result = service.isAuthenticated();

      expect(result).toBe(false);
      expect(authStoreSpy.isJwtExpired).not.toHaveBeenCalled();
    });

    it('should return false if token is expired', () =>{
      mockIsLoggedIn.set(true);
      authStoreSpy.isJwtExpired.and.returnValue(true)

      const result = service.isAuthenticated();

      expect(result).toBe(false);
      expect(authStoreSpy.isJwtExpired).toHaveBeenCalled();
    });
  })


  it('should return the logged in users username', () =>{
    authStoreSpy.getUsername.and.returnValue("skaran");

    service.getUsername();

    expect(authStoreSpy.getUsername).toHaveBeenCalled();
  });

  describe('effect', () => {
    it('should navigate to login when not logged in and status is loaded', fakeAsync(() => {
      TestBed.runInInjectionContext(() => {
        // initial state
        mockIsLoggedIn.set(false);
        mockStatus.set('pending');

        // service initializes
        const instance = TestBed.inject(AuthService);
        tick(); // process effects
        expect(routerSpy.navigate).not.toHaveBeenCalled();


        // status becomes 'loaded'
        mockStatus.set('loaded');
        tick(); // process effects again
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/auth/login']);
      });
    }));

    it('should not navigate when logged in and status is loaded', fakeAsync(() => {
      TestBed.runInInjectionContext(() => {
        mockIsLoggedIn.set(true);
        mockStatus.set('loaded');

        const instance = TestBed.inject(AuthService);
        tick();

        expect(routerSpy.navigate).not.toHaveBeenCalled();
      });
    }));
  });
})
