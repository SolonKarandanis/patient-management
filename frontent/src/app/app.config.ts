import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import { MessageService } from 'primeng/api';
import Aura from '@primeng/themes/aura';
import {TranslateHttpLoader} from '@ngx-translate/http-loader'
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';

import { routes } from './app.routes';
import {HttpBackend, HttpClient, provideHttpClient} from "@angular/common/http";
import {firstValueFrom} from "rxjs";

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
      provideZoneChangeDetection({ eventCoalescing: true }),
      provideRouter(routes),
      provideAnimationsAsync(),
      providePrimeNG({
        theme: {
          preset: Aura
        }
      }),
      provideHttpClient(),
      importProvidersFrom(TranslateModule.forRoot(provideTranslation())),
  ]
};


export function createTranslateLoader(httpHandler: HttpBackend): TranslateHttpLoader {
  return  new  TranslateHttpLoader(new HttpClient(httpHandler), './assets/i18n/', '.json');
}

export function appInitializerFactory(translate: TranslateService) {
  return (): Promise<any> => {
    translate.setDefaultLang('en');
    return firstValueFrom(translate.use('en'));
  };
}
