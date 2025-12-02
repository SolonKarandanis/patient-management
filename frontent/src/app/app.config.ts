import {
  ApplicationConfig,
  importProvidersFrom,
  provideZoneChangeDetection
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import {ConfirmationService, MessageService} from 'primeng/api';
import Aura from '@primeng/themes/aura';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';

import { routes } from './app.routes';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withInterceptors,
  withInterceptorsFromDi
} from "@angular/common/http";
import {ErrorService} from '@core/services/error.service';
import {BaseUrlInterceptor} from '@core/interceptors/base-url.interceptor';
import {LanguageInterceptor} from '@core/interceptors/language.interceptor';
import {httpError} from '@core/interceptors/http-error.interceptor';
import {authExpired} from '@core/interceptors/auth-expired.interceptor';
import {TranslationController} from '@core/repositories/translation.controller';
import {CustomTranslateLoader} from '@core/helpers/translation.loader';

export function createCustomTranslateLoader(translationController: TranslationController): CustomTranslateLoader {
  return new CustomTranslateLoader(translationController);
}

export const provideTranslation = () => ({
  loader: {
    provide: TranslateLoader,
    useFactory: createCustomTranslateLoader,
    deps: [TranslationController],
  },
});

export const appConfig: ApplicationConfig = {
  providers: [
    MessageService,
    ErrorService,
    ConfirmationService,
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
  ]
};
