import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {I18nResourceResponse, Language, UpdateI18nResource} from '@models/i18n-resource.model';
import {ApiRepositories} from '@core/repositories/ApiRepositories';
import {inject, Service} from '@angular/core';
import {I18nResourceSearchRequest} from '@models/search.model';

@Service()
export class I18nTranslationRepository {

  private readonly http = inject(HttpClient);

  getLanguages(): Observable<Language[]> {
    return this.http.get<Language[]>(`${ApiRepositories.I18N}/languages`);
  }

  getModules(): Observable<Record<number,string>> {
    return this.http.get<Record<number,string>>(`${ApiRepositories.I18N}/modules`);
  }

  updateTranslations(updates: UpdateI18nResource[]):Observable<void> {
    return this.http.put<void>(`${ApiRepositories.I18N}`, updates);
  }

  searchResources(searchRequest: I18nResourceSearchRequest):Observable<I18nResourceResponse> {
    return this.http.post<I18nResourceResponse>(`${ApiRepositories.I18N}/search`, searchRequest );
  }

}
