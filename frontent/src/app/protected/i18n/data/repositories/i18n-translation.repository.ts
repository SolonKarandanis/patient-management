import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {I18nResourceResponse, Language, UpdateI18nResource} from '@models/i18n-resource.model';
import {ApiRepositories} from '@core/repositories/ApiRepositories';
import {Injectable} from '@angular/core';
import {I18nResourceSearchRequest} from '@models/search.model';

@Injectable({
  providedIn: 'root',
})
export class I18nTranslationRepository {

  constructor(private readonly http: HttpClient) {}

  getLanguages(): Observable<Language[]> {
    return this.http.get<Language[]>(`${ApiRepositories.I18N}/languages`);
  }

  getModules(): Observable<string[]> {
    return this.http.get<string[]>(`${ApiRepositories.I18N}/modules`);
  }

  updateTranslations(updates: UpdateI18nResource[]):Observable<void> {
    return this.http.put<void>(`${ApiRepositories.I18N}`, updates);
  }

  searchResources(searchRequest: I18nResourceSearchRequest):Observable<I18nResourceResponse> {
    return this.http.post<I18nResourceResponse>(`${ApiRepositories.I18N}/search`, searchRequest );
  }

}
