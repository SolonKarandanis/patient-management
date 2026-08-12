import {inject, Injectable} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {
  ActivatedRouteSnapshot,
  GuardResult,
  MaybeAsync,
  RouterStateSnapshot,
  CanActivate
} from '@angular/router';
import {toObservable} from '@angular/core/rxjs-interop';
import {filter, map, take} from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  private readonly authService = inject(AuthService);

  // Must be created here (a field initializer, run during DI construction —
  // an injection context) rather than inside canActivate(), which the Router
  // invokes as a plain method call: toObservable() asserts an injection
  // context and throws NG0203 otherwise.
  private readonly status$ = toObservable(this.authService.status);

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    if (this.authService.isAuthenticated()) {
      return true;
    }

    // Auth state may still be resolving (e.g. right after an OAuth2 redirect back
    // from Keycloak: the token exchange has completed, but authStore.isLoggedIn()
    // only flips to true once the follow-up user/permissions calls resolve).
    // Wait for status to settle instead of sampling isAuthenticated() once.
    // If it settles to "still not authenticated", AuthService's own effect
    // (constructor) already redirects appropriately per auth mode — this guard
    // only needs to block the route, not navigate itself.
    return this.status$.pipe(
      filter(status => status === 'loaded' || status === 'error'),
      take(1),
      map(() => this.authService.isAuthenticated()),
    );
  }

}
