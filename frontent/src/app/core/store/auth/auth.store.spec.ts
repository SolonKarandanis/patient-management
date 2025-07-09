import { AuthStore } from "./auth.store";
import {TestBed} from '@angular/core/testing';
import {of} from 'rxjs';
import {mockJwt, mockLoginCredentials, mockUser} from '@testing/mockData';
import { AuthRepository } from "@core/repositories/auth.repository";
import {JwtUtil} from '@core/services/jwt-util.service';
import {RolesConstants} from '@core/guards/SecurityConstants';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {UtilService} from '@core/services/util.service';

type AuthStore = InstanceType<typeof AuthStore>;

describe('AuthStore', () =>{
  let store: AuthStore;
  let authRepoSpy: jasmine.SpyObj<AuthRepository>;
  let jwtUtilSpy: jasmine.SpyObj<JwtUtil>;
  let utilServiceSpy: jasmine.SpyObj<UtilService>;

  beforeEach(()=>{
    authRepoSpy = jasmine.createSpyObj('AuthRepository',[
      'login',
      'getUserByToken',
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

    TestBed.configureTestingModule({
      imports:[
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader }
        })
      ],
      providers:[
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

    store.login(mockLoginCredentials);

    expect(authRepoSpy.login).toHaveBeenCalledWith(mockLoginCredentials);
    expect(authRepoSpy.login).toHaveBeenCalledTimes(1);
  });

  it('should get user account by token ', () =>{
    authRepoSpy.getUserByToken.and.returnValue(of(mockUser));

    store.getUserAccount();

    expect(authRepoSpy.getUserByToken).toHaveBeenCalled();
    expect(authRepoSpy.getUserByToken).toHaveBeenCalledTimes(1);
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
    expect(store.errorMessage()).toBe(null);
    expect(store.showError()).toBe(false);
    expect(store.loading()).toBe(false);
  });

  it('should set account info from storage ', () =>{
    store.setAccountInfoFromStorage(mockJwt.token,mockJwt.expires,mockUser);

    expect(store.authToken()).toBe(mockJwt.token);
    expect(store.expires()).toBe(mockJwt.expires);
    expect(store.user()).toBe(mockUser);
    expect(store.isLoggedIn()).toBe(true);
  });

  it('should set account ', () =>{
    store.setAccount(mockUser);

    expect(store.user()).toBe(mockUser);
    expect(store.isLoggedIn()).toBe(true);
    expect(store.errorMessage()).toBe(null);
    expect(store.showError()).toBe(false);
    expect(store.loading()).toBe(false);
  });

  it('should logout ', () =>{
    store.logout();

    expect(store.user()).toBe(undefined);
    expect(store.isLoggedIn()).toBe(false);
    expect(store.errorMessage()).toBe(null);
    expect(store.showError()).toBe(false);
    expect(store.loading()).toBe(false);
    expect(store.authToken()).toBe(undefined);
    expect(store.expires()).toBe(undefined);
  });
})
