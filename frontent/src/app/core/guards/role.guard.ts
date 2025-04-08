import {inject, Injectable, Signal} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {TranslateService} from '@ngx-translate/core';
import {MessageService} from 'primeng/api';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  GuardResult,
  MaybeAsync,
  RouterStateSnapshot
} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);
  private readonly translate = inject(TranslateService);

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    const isAuthorized = this.isAuthorized(route);
    if(!isAuthorized()){
      this.messageService.add({
        severity: 'error',
        summary: this.translate.instant('GLOBAL.ERRORS.SUMMARY'),
        detail: this.translate.instant('GLOBAL.ERRORS.FORBIDDEN'),
      })
    }
    return isAuthorized();
  }

  private isAuthorized(route:ActivatedRouteSnapshot):Signal<boolean>{
    const allowedRoles = route.data['allowedRoles'] as string[];
    return this.authService.hasAnyAuthority(allowedRoles);
  }
}
