import {
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
  HttpEvent,
  HttpResponse,
  HttpErrorResponse,
} from "@angular/common/http";
import {inject} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {JwtUtil} from '@core/services/jwt-util.service';
import {AUTHENTICATE_REQUEST} from '@core/guards/SecurityConstants';
import {tap} from 'rxjs';

export const authExpired: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const authService = inject(AuthService);
  const jwtUtil = inject(JwtUtil);
  const userToken = jwtUtil.getToken();
  if (request.context.get(AUTHENTICATE_REQUEST)){
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${userToken}`,
      },
      // withCredentials:true
    });
  }

  return next(request).pipe(
    tap({
      next: (event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          // console.log('all looks good');
          // http response status code
          // console.log(event.status);
        }
      },
      error: (error) => {
        if(error instanceof HttpErrorResponse){
          if(error.status === 401){
            // redirect to login
            authService.logout()
          }

        }
      }
    })
  )
}
