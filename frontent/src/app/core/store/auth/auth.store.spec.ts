import { AuthStore } from "./auth.store";
import {TestBed} from '@angular/core/testing';
import {EMPTY, of} from 'rxjs';
import {mockJwt, mockLoginCredentials, mockUser} from '@testing/mockData';
import { AuthRepository } from "@core/repositories/auth.repository";
import {JwtUtil} from '@core/services/jwt-util.service';
import {RolesConstants} from '@core/guards/SecurityConstants';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {UtilService} from '@core/services/util.service';
import {NgxPermissionsService} from 'ngx-permissions';

type AuthStore = InstanceType<typeof AuthStore>;

describe('AuthStore', () =>{
  let store: AuthStore;
  let authRepoSpy: jasmine.SpyObj<AuthRepository>;
  let jwtUtilSpy: jasmine.SpyObj<JwtUtil>;
  let utilServiceSpy: jasmine.SpyObj<UtilService>;
  let permissionsServiceSpy: jasmine.SpyObj<NgxPermissionsService>;

  beforeEach(()=>{
    authRepoSpy = jasmine.createSpyObj('AuthRepository',[
      'login',
      'getUserByToken',
      'getUserPermissions',
    ]);

    jwtUtilSpy = jasmine.createSpyObj('JwtUtil',[
      'getToken',
      'getTokenExpiration',
      'getUser',
      'isJwtExpired',
      'saveToken',
      'saveTokenExpiration',
      'destroyToken',
      'destroyTokenExpiration'
    ]);

    utilServiceSpy = jasmine.createSpyObj('UtilService',[
      'showMessage',
    ]);

    permissionsServiceSpy = jasmine.createSpyObj('NgxPermissionsService', [
      'loadPermissions',
    ]);

    TestBed.configureTestingModule({
      imports:[
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader }
        })
      ],
      providers:[
        AuthStore,
        {
          provide: AuthRepository,
          useValue: authRepoSpy,
        },
        {
          provide: JwtUtil,
          useValue: jwtUtilSpy,
        },
        {
          provide: UtilService,
          useValue: utilServiceSpy,
        },
        {
          provide: NgxPermissionsService,
          useValue: permissionsServiceSpy,
        }
      ]
    });

    store = TestBed.inject(AuthStore);
  });

  it('should be created', () => {
    expect(store).toBeTruthy();
  });

  it('should perform login ', () =>{
    authRepoSpy.login.and.returnValue(of(mockJwt));
    authRepoSpy.getUserByToken.and.returnValue(of(mockUser));
    authRepoSpy.getUserPermissions.and.returnValue(of(mockUser.operations.map(op => op.name)));

    store.login(mockLoginCredentials);

    expect(authRepoSpy.login).toHaveBeenCalledWith(mockLoginCredentials);
    expect(authRepoSpy.getUserByToken).toHaveBeenCalled();
    expect(authRepoSpy.getUserPermissions).toHaveBeenCalledWith(mockUser.publicId);
    expect(permissionsServiceSpy.loadPermissions).toHaveBeenCalledWith(mockUser.operations.map(op => op.name));
    expect(store.status()).toBe('loaded');
    expect(store.isLoggedIn()).toBe(true);
    expect(store.user()).toEqual(mockUser);
  });

  describe('initAuth', () => {
    it('should login user if token is valid', () => {
      jwtUtilSpy.isJwtExpired.and.returnValue(false);
      jwtUtilSpy.getToken.and.returnValue(mockJwt.token);
      jwtUtilSpy.getTokenExpiration.and.returnValue(mockJwt.expires);
      authRepoSpy.getUserByToken.and.returnValue(of(mockUser));
      authRepoSpy.getUserPermissions.and.returnValue(of(mockUser.operations.map(op => op.name)));

      store.initAuth();

      expect(authRepoSpy.getUserByToken).toHaveBeenCalled();
      expect(authRepoSpy.getUserPermissions).toHaveBeenCalledWith(mockUser.publicId);
      expect(permissionsServiceSpy.loadPermissions).toHaveBeenCalledWith(mockUser.operations.map(op => op.name));
      expect(store.status()).toBe('loaded');
      expect(store.isLoggedIn()).toBe(true);
      expect(store.user()).toEqual(mockUser);
    });

    it('should logout user if token is invalid', () => {
      jwtUtilSpy.isJwtExpired.and.returnValue(true);

      store.initAuth();

      expect(authRepoSpy.getUserByToken).not.toHaveBeenCalled();
      expect(store.isLoggedIn()).toBe(false);
      expect(store.user()).toBe(undefined);
    });
  });


  it('should verify that it should return computed user ', () =>{
    store.setAccount(mockUser);

    expect(store.getUser()).toBe(mockUser);
  });

  it('should verify that it should return computed user username ', () =>{
    store.setAccount(mockUser);

    expect(store.getUsername()).toBe(mockUser.username);
  });

  it('should verify that  the user has the given authority', () =>{
    store.setAccount(mockUser);

    expect(store.hasAnyAuthority(RolesConstants.ROLE_ADMIN)()).toBe(true);
  });

  it('should verify that the user does not have the given authority', () =>{
    store.setAccount(mockUser);

    expect(store.hasAnyAuthority(RolesConstants.ROLE_DOCTOR)()).toBe(false);
  });

  it('should set token details ', () =>{
    store.setTokenDetails(mockJwt.token,mockJwt.expires);

    expect(store.authToken()).toBe(mockJwt.token);
    expect(store.expires()).toBe(mockJwt.expires);
    expect(store.isLoggedIn()).toBe(false);
    expect(store.error()).toBe(null);
    expect(store.status()).toBe('pending');
  });

  it('should set account ', () =>{
    store.setAccount(mockUser);

    expect(store.user()).toBe(mockUser);
    expect(store.isLoggedIn()).toBe(true);
    expect(store.error()).toBe(null);
  });

  it('should logout ', () =>{
    store.logout();

    expect(store.user()).toBe(undefined);
    expect(store.isLoggedIn()).toBe(false);
    expect(store.error()).toBe(null);
    expect(store.status()).toBe('loaded');
    expect(store.authToken()).toBe(undefined);
    expect(store.expires()).toBe(undefined);
  });
})
