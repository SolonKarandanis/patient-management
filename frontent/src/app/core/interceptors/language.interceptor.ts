import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {LanguageService} from '@core/services/language.service';


@Injectable()
export class LanguageInterceptor implements HttpInterceptor{
  constructor(private readonly languageService: LanguageService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const newReq = req.clone({
      setHeaders: {
        ['Lang-ISO']: this.languageService.selectedLanguageIso,
      },
    });
    return next.handle(newReq);
  }

}
