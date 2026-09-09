
import { AuthStore } from '../store/auth/auth.store';
import {effect, inject, Service, Signal, untracked} from '@angular/core';
import {GenericService} from './generic.service';
import {Router} from '@angular/router';
import {User} from '@models/user.model';
import {SubmitCredentialsDTO} from '@models/auth.model';
import {UserRoles} from '@models/constants';
import {AuthModeService} from '@core/services/auth-mode.service';
import {OAuthConfigService} from '@core/services/oauth-config.service';

type AuthStore = InstanceType<typeof AuthStore>;

@Service()
export class AuthService extends GenericService{

  public isLoading:Signal<boolean>;
  public isLoggedIn:Signal<boolean>;
  public loggedUser:Signal<User | undefined>;
  public loggedUserId: Signal<string| undefined>;
  public loggedInUserRoleIds:Signal<number[]>;
  public status: Signal<'pending' | 'loading' | 'loaded' | 'error'>;

  private readonly authStore = inject(AuthStore);
  private readonly router = inject(Router);
  private readonly authModeService = inject(AuthModeService);
  private readonly oauthConfigService = inject(OAuthConfigService);

  constructor(){
    super()
    this.isLoading = this.authStore.loading;
    this.isLoggedIn = this.authStore.isLoggedIn;
    this.loggedUser=this.authStore.getUser;
    this.loggedUserId=this.authStore.getUserId;
    this.loggedInUserRoleIds = this.authStore.getRoleIds;
    this.status = this.authStore.status;

    effect(()=>{
      const loggedIn = this.isAuthenticated();
      const status = this.status();
      if(!loggedIn && status === 'loaded'){
        untracked(()=>{
          if (this.authModeService.isOAuth2()) {
            this.oauthConfigService.login();
          } else {
            this.navigateToLogin();
          }
        });
      }
    });
  }

  public initAuth(): void {
    if (this.authModeService.isOAuth2()) {
      if (this.oauthConfigService.isAuthenticated()) {
        this.authStore.loadUserAndPermissions();
      } else {
        this.authStore.logout();
      }
    } else {
      this.authStore.initAuth();
    }
  }

  private navigateToHome():void{
    this.router.navigate(['/home'], {
      queryParams: {},
    });
  }

  private navigateToLogin():void{
    this.router.navigate(['/auth/login'], {
      queryParams: {},
    });
  }

  // public methods
  public login(credentials:SubmitCredentialsDTO):void{
    if (this.authModeService.isOAuth2()) {
      this.oauthConfigService.login();
    } else {
      this.authStore.login(credentials);
    }
  }

  public logout() {
    this.authStore.logout();
    if (this.authModeService.isOAuth2()) {
      this.oauthConfigService.logout();
    } else {
      this.navigateToLogin();
    }
  }

  public hasAnyAuthority(authorities: string[] | string):Signal<boolean>{
    return this.authStore.hasAnyAuthority(authorities);
  }

  /**
   * Checks if the user is loggedin
   * @returns  if the user is loggedin
   */
  public isAuthenticated():boolean{
    if (this.authModeService.isOAuth2()) {
      return this.oauthConfigService.isAuthenticated() && this.authStore.isLoggedIn();
    }
    return this.authStore.isLoggedIn() && !this.authStore.isJwtExpired();
  }


  public getUsername():string |null{
    const storeValue=this.authStore.getUsername();
    if(storeValue){
      return storeValue;
    }
    return null;
  }

  public hasRole(role:UserRoles):Signal<boolean>{
    return this.authStore.hasRole(role);
  }

  public isUserMe(userId:string| undefined):Signal<boolean>{
    return this.authStore.isUserMe(userId);
  }
}
