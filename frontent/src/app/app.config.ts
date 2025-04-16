import {
  ApplicationConfig,
  importProvidersFrom, inject,
  provideAppInitializer,
  provideZoneChangeDetection
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import { MessageService } from 'primeng/api';
import Aura from '@primeng/themes/aura';
import {TranslateHttpLoader} from '@ngx-translate/http-loader'
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';

import { routes } from './app.routes';
import {
  HTTP_INTERCEPTORS,
  HttpBackend,
  HttpClient,
  provideHttpClient,
  withInterceptors,
  withInterceptorsFromDi
} from "@angular/common/http";
import {firstValueFrom} from "rxjs";
import {ErrorService} from '@core/services/error.service';
import {BaseUrlInterceptor} from '@core/interceptors/base-url.interceptor';
import {LanguageInterceptor} from '@core/interceptors/language.interceptor';
import {httpError} from '@core/interceptors/http-error.interceptor';
import {authExpired} from '@core/interceptors/auth-expired.interceptor';

export const provideTranslation = () => ({
  defaultLanguage: 'en',
  loader: {
    provide: TranslateLoader,
    useFactory: createTranslateLoader,
    deps: [HttpBackend],
  },
});

export const appConfig: ApplicationConfig = {
  providers: [
    MessageService,
    ErrorService,
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: Aura
      }
    }),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: BaseUrlInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: LanguageInterceptor,
      multi: true,
    },
    provideHttpClient(
      withInterceptors([
        httpError,
        authExpired,
      ]),
      withInterceptorsFromDi(),
    ),
    importProvidersFrom(TranslateModule.forRoot(provideTranslation())),
    provideAppInitializer(appInitializerFactory),

  ]
};


export function createTranslateLoader(httpHandler: HttpBackend): TranslateHttpLoader {
  return  new  TranslateHttpLoader(new HttpClient(httpHandler), './assets/i18n/', '.json');
}

export function appInitializerFactory() {
  const translate = inject(TranslateService);
  translate.setDefaultLang('en');
  return firstValueFrom(translate.use('en'));
}
