import {inject, Injectable} from '@angular/core';
import {I18nResourceStore} from '../store/i18n.store';
import {SearchService} from '@core/services/search.service';
import {FormControl, FormGroup} from '@angular/forms';
import {I18nResource, UpdateI18nResource} from '@models/i18n-resource.model';
import {I18nResourceSearchForm} from '../../forms';
import {GenericService} from '@core/services/generic.service';

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

  public executeSearchResources(searchForm:FormGroup<I18nResourceSearchForm>):void{
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

  public initSearchI18nResourceForm(): FormGroup<I18nResourceSearchForm>{
    return this.formBuilder.group<I18nResourceSearchForm>({
      language: new FormControl(null),
      module:  new FormControl(null),
      term:  new FormControl(null),
      rows:new FormControl(10,{nonNullable: true}),
      first: new FormControl(0,{nonNullable: true}),
      sortField: new FormControl('',{nonNullable: true}),
      sortOrder: new FormControl('ASC',{nonNullable: true})
    });
  }
}
