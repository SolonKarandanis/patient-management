import {Injectable} from '@angular/core';
import {BaseRepository} from '@core/repositories/BaseRepository';
import {Observable} from 'rxjs';
import {ApiRepositories} from '@core/repositories/ApiRepositories';
import { HttpParams } from '@angular/common/http';


@Injectable({
  providedIn: 'root',
})
export class TranslationController extends BaseRepository{
  getTranslations(languageIsoCode: string): Observable<any> {
    let params: HttpParams = new HttpParams();
    params = params.append('languageIsoCode', languageIsoCode);
    return this.http.get(`${ApiRepositories.I18N}/ui-labels`, { params });
  }
}
