import {
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
  HttpErrorResponse,
} from "@angular/common/http";
import {inject} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {JwtUtil} from '@core/services/jwt-util.service';
import {AUTHENTICATE_REQUEST} from '@core/guards/SecurityConstants';
import {AuthModeService} from '@core/services/auth-mode.service';
import {OAuthConfigService} from '@core/services/oauth-config.service';
import {tap} from 'rxjs';

export const authExpired: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const authService = inject(AuthService);
  const jwtUtil = inject(JwtUtil);
  const authModeService = inject(AuthModeService);
  const oauthConfigService = inject(OAuthConfigService);

  if (request.context.get(AUTHENTICATE_REQUEST)) {
    const token = authModeService.isOAuth2()
      ? oauthConfigService.getAccessToken()
      : jwtUtil.getToken();
    if (token) {
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      });
    }
  }

  return next(request).pipe(
    tap({
      error: (error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          authService.logout();
        }
      },
    })
  );
}
