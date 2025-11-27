import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {I18nResource, I18nResourceResponse, UpdateI18nResource} from '@models/i18n-resource.model';
import {ApiRepositories} from '@core/repositories/ApiRepositories';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class I18nTranslationController {

  constructor(private readonly http: HttpClient) {}

  getLanguages(): Observable<string[]> {
    return this.http.get<string[]>(`${ApiRepositories.I18N}/languages`);
  }

  getModules(): Observable<string[]> {
    return this.http.get<string[]>(`${ApiRepositories.I18N}/modules`);
  }

  updateTranslations(updates: UpdateI18nResource[]) {
    return this.http.put<I18nResource>(`${ApiRepositories.I18N}`, updates);
  }

  searchResources(searchParams: any) {
    let params: HttpParams = { ...searchParams };
    return this.http.post<I18nResourceResponse>(`${ApiRepositories.I18N}/search`, params );
  }

}
