import {inject, Injectable, signal} from '@angular/core';
import {I18nLookupStore} from '../store/i18n-lookup.store';
import {I18nResourceSearchStore} from '../store/i18n-resource-search.store';
import {I18nResourceDetailStore} from '../store/i18n-resource-detail.store';
import {SearchService} from '@core/services/search.service';
import {I18nResource, UpdateI18nResource} from '@models/i18n-resource.model';
import { I18nResourceSearchFormModel} from '../../forms';
import {GenericService} from '@core/services/generic.service';

import {FieldTree, form} from '@angular/forms/signals';
import {SearchTypesEnum} from '@models/constants';


@Injectable({
  providedIn: 'root'
})
export class I18nTranslationService extends GenericService{

  private i18nLookupStore = inject(I18nLookupStore);
  private i18nResourceSearchStore = inject(I18nResourceSearchStore);
  private i18nResourceDetailStore = inject(I18nResourceDetailStore);
  private searchService = inject(SearchService);

  public languagesAsSelectItems = this.i18nLookupStore.getLanguagesAsSelectItems;
  public modulesAsSelectItems = this.i18nLookupStore.getModulesAsSelectItems;
  public searchResults = this.i18nResourceSearchStore.searchResults;
  public totalCount = this.i18nResourceSearchStore.totalCount;
  public isSearchLoading = this.i18nResourceSearchStore.loading;
  public isDetailLoading = this.i18nResourceDetailStore.loading;
  public criteriaCollapsed = this.i18nResourceSearchStore.criteriaCollapsed;
  public hasSearched = this.i18nResourceSearchStore.hasSearched;
  public tableLoading = this.i18nResourceSearchStore.tableLoading;

  public executeSearchResources(searchForm: FieldTree<I18nResourceSearchFormModel, string | number>):void{
    const request = this.searchService.toI18nResourceSearchRequest(searchForm);
    this.i18nResourceSearchStore.searchResources(request);
  }

  public executeGetModules():void{
    this.i18nLookupStore.getModules();
  }

  public executeGetLanguages():void{
    this.i18nLookupStore.getLanguages();
  }

  public executeUpdateResources(request:UpdateI18nResource[],row:I18nResource):void{
    this.i18nResourceDetailStore.updateTranslations({updates:request,row:row});
  }

  private searchI18nModel = signal<I18nResourceSearchFormModel>({
    searchMethod:SearchTypesEnum.SEARCH_TYPE_AND,
    language:null,
    module:null,
    term:'',
    rows: 10,
    first:0,
    sortField:'key',
    sortOrder: "ASC"
  });

  public initSearchI18nResourceForm(): FieldTree<I18nResourceSearchFormModel, string | number>{
    return form<I18nResourceSearchFormModel>(this.searchI18nModel);
  }
}
