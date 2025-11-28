import {inject, Injectable} from '@angular/core';
import {I18nResourceStore} from '../store/i18n.store';
import {SearchService} from '@core/services/search.service';
import {TranslateService} from '@ngx-translate/core';
import {UtilService} from '@core/services/util.service';
import {FormGroup} from '@angular/forms';
import {UpdateI18nResource} from '@models/i18n-resource.model';

@Injectable({
  providedIn: 'root'
})
export class I18nTranslationService {

  private resourceStore = inject(I18nResourceStore);
  private searchService = inject(SearchService);
  private translateService = inject(TranslateService);
  private utilService = inject(UtilService);

  public languages = this.resourceStore.languages;
  public modules = this.resourceStore.modules;
  public searchResults = this.resourceStore.searchResults;
  public totalCount = this.resourceStore.totalCount;
  public isLoading = this.resourceStore.loading;
  public criteriaCollapsed = this.resourceStore.criteriaCollapsed;
  public hasSearched = this.resourceStore.hasSearched;
  public tableLoading = this.resourceStore.tableLoading;

  public executeSearchResources(searchForm:FormGroup):void{
    const request = this.searchService.toI18nResourceSearchRequest(searchForm);
    this.resourceStore.searchResources(request);
  }

  public executeGetModules():void{
    this.resourceStore.getModules();
  }

  public executeGetLanguages():void{
    this.resourceStore.getLanguages();
  }

  public executeUpdateResources(request:UpdateI18nResource[]):void{
    this.resourceStore.updateTranslations(request);
  }
}
