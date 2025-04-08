import {Inject, Injectable} from '@angular/core';
import {API_BASE_URL} from '@core/token';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable()
export class BaseUrlInterceptor implements HttpInterceptor{
  constructor(@Inject(API_BASE_URL) private readonly baseUrl: string) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.includes('/assets/i18n')) {
      return next.handle(req);
    }
    const apiReq = req.clone({ url: `${this.baseUrl}/${req.url}` });
    return next.handle(apiReq);
  }

}
