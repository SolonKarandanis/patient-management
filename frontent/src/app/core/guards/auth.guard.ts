import {inject, Injectable} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {
  ActivatedRouteSnapshot,
  GuardResult,
  MaybeAsync,
  Router,
  RouterStateSnapshot,
  CanActivate
} from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    const isAuthenticated = this.authService.isAuthenticated();
    if(!isAuthenticated){
      this.navigateToLogin();
    }
    return isAuthenticated;
  }

  private navigateToLogin():void{
    this.router.createUrlTree(['/auth/login']);
  }

}
