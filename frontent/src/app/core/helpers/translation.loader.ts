import {TranslateLoader} from '@ngx-translate/core';
import {TranslationController} from '@core/repositories/translation.controller';
import {Observable} from 'rxjs';

export class CustomTranslateLoader implements TranslateLoader {
  constructor(private readonly translationController: TranslationController) {}

  getTranslation(languageIsoCode: string): Observable<any> {
    return this.translationController.getTranslations(languageIsoCode);
  }
}
