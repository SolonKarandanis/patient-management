import {TranslateLoader} from '@ngx-translate/core';
import {TranslationRepository} from '@core/repositories/translation.repository';
import {Observable} from 'rxjs';

export class CustomTranslateLoader implements TranslateLoader {
  constructor(private readonly translationRepository: TranslationRepository) {}

  getTranslation(languageIsoCode: string): Observable<any> {
    return this.translationRepository.getTranslations(languageIsoCode);
  }
}
