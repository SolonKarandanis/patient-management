import {inject, Injectable, signal} from '@angular/core';
import {I18nResourceStore} from '../store/i18n.store';
import {SearchService} from '@core/services/search.service';
import { FormGroup} from '@angular/forms';
import {I18nResource, UpdateI18nResource} from '@models/i18n-resource.model';
import {I18nResourceSearchForm, I18nResourceSearchFormModel} from '../../forms';
import {GenericService} from '@core/services/generic.service';

import {FieldTree, form} from '@angular/forms/signals';


@Injectable({
  providedIn: 'root'
})
export class I18nTranslationService extends GenericService{

  private resourceStore = inject(I18nResourceStore);
  private searchService = inject(SearchService);

  public languagesAsSelectItems = this.resourceStore.getLanguagesAsSelectItems;
  public modulesAsSelectItems = this.resourceStore.getModulesAsSelectItems;
  public searchResults = this.resourceStore.searchResults;
  public totalCount = this.resourceStore.totalCount;
  public isLoading = this.resourceStore.loading;
  public criteriaCollapsed = this.resourceStore.criteriaCollapsed;
  public hasSearched = this.resourceStore.hasSearched;
  public tableLoading = this.resourceStore.tableLoading;

  public executeSearchResources(searchForm: FieldTree<I18nResourceSearchFormModel, string | number>):void{
    const request = this.searchService.toI18nResourceSearchRequest(searchForm);
    this.resourceStore.searchResources(request);
  }

  public executeGetModules():void{
    this.resourceStore.getModules();
  }

  public executeGetLanguages():void{
    this.resourceStore.getLanguages();
  }

  public executeUpdateResources(request:UpdateI18nResource[],row:I18nResource):void{
    this.resourceStore.updateTranslations({updates:request,row:row});
  }

  private searchI18nModel = signal<I18nResourceSearchFormModel>({
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
