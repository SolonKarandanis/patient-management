import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {FormBuilder, FormControl, ReactiveFormsModule} from '@angular/forms';
import {UserSearchForm} from '../forms';
import {FloatLabel} from 'primeng/floatlabel';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {InputText} from 'primeng/inputtext';
import {TranslatePipe} from '@ngx-translate/core';
import {SelectItem} from 'primeng/api';
import {Select} from 'primeng/select';
import {SearchButtonsComponent} from '@components/search-buttons/search-buttons.component';
import {TableLazyLoadEvent} from 'primeng/table';
import {SavedSearch, SearchTableColumn, SearchType} from '@models/search.model';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {UserService} from '../data/services/user.service';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {FieldsetModule} from 'primeng/fieldset';
import {ResultsTableComponent} from '@components/results-table/results-table.component';
import {ResultsTableFilterDirective} from '@directives/results-table-filter.directive';
import {ResultsTablePaginatorDirective} from '@directives/results-table-paginator.directive';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';
import {fadeAnimation} from '@shared/animations/fadeAnimation';

@Component({
  selector: 'app-search-users',
  imports: [
    PageHeaderComponent,
    ReactiveFormsModule,
    FloatLabel,
    FormErrorComponent,
    InputText,
    TranslatePipe,
    Select,
    SearchButtonsComponent,
    RequiredFieldsLabelComponent,
    FieldsetModule,
    ResultsTableComponent,
    ResultsTableFilterDirective,
    ResultsTablePaginatorDirective,
    FieldsetComponent,
  ],
  template: `
    <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0">
      <app-page-header>
        {{ 'USER.SEARCH.title' | translate }}
      </app-page-header>
      <app-required-fields-label></app-required-fields-label>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
        <div role="search">
          <app-fieldset legend="{{ 'SEARCH.COMMON.search-criteria' | translate }}"
                        [toggleable]="true"
                        [collapsed]="criteriaCollapsed()">
            <form [formGroup]="form">
              <div class="grid gap-6 mt-6 md:grid-cols-3">
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <input
                      id="email"
                      pInputText
                      type="email"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      formControlName="email"
                      autocomplete="email"/>
                    <label for="email">{{ 'USER.SEARCH.LABELS.email' | translate }}</label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="isFieldValid('email')"
                    [validationErrors]="form.get('email')?.errors" />
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <input
                      id="username"
                      pInputText
                      type="text"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      formControlName="username"
                      autocomplete="username"/>
                    <label for="username">{{ 'USER.SEARCH.LABELS.username' | translate }}</label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="isFieldValid('username')"
                    [validationErrors]="form.get('username')?.errors" />
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <input
                      id="name"
                      pInputText
                      type="text"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      formControlName="name"
                      autocomplete="name"/>
                    <label for="name">{{ 'USER.SEARCH.LABELS.name' | translate }}</label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="isFieldValid('name')"
                    [validationErrors]="form.get('name')?.errors" />
                </div>
              </div>
              <div class="grid gap-6 mt-6 md:grid-cols-2">
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <p-select
                      formControlName="status"
                      [options]="userStatuses"
                      [checkmark]="true"
                      [showClear]="true"
                      class="border-0 !bg-white text-sm shadow w-full"/>
                    <label class="app-required-label" for="name">
                      {{ 'USER.SEARCH.LABELS.status' | translate }}
                    </label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="isFieldValid('status')"
                    [validationErrors]="form.get('status')?.errors" />
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <p-select
                      formControlName="role"
                      [options]="commonEntitiesService.rolesAsSelectItems()"
                      [checkmark]="true"
                      [showClear]="true"
                      class="border-0 !bg-white text-sm shadow w-full"/>
                    <label class="app-required-label" for="name">
                      {{ 'USER.SEARCH.LABELS.role' | translate }}
                    </label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="isFieldValid('role')"
                    [validationErrors]="form.get('role')?.errors" />
                </div>
              </div>
              <app-search-buttons #searchBtns
                                  [searchType]="searchType"
                                  [enableSaveSearch]="true"
                                  [searchForm]="form"
                                  (searchClicked)="search()"
                                  (resetClicked)="resetForm()"/>
            </form>
          </app-fieldset>
          @if (hasSearched()){
            <div @fadeAnimation class="mt-6">
                <app-results-table
                    tableFilter
                    tablePaginator
                    [colTitles]="tableColumns"
                    [tableItems]="results()"
                    [totalRecords]="totalCount()"
                    [resultsPerPage]="form.controls['rows'].value"
                    (tableStateChanged)="handleTableLazyLoad($event)"
                    [first]="form.controls['first'].value"
                    [lazy]="true"
                    [loading]="tableLoading()"
                />
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styleUrl: './search-users.component.css',
  animations:[fadeAnimation],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchUsersComponent extends BaseComponent implements OnInit{
  protected fb= inject(FormBuilder);
  private userService = inject(UserService);
  protected commonEntitiesService = inject(CommonEntitiesService);

  protected results=this.userService.searchResults;
  protected totalCount=this.userService.totalCount;
  protected criteriaCollapsed=this.userService.criteriaCollapsed;
  protected tableLoading=this.userService.tableLoading;
  protected loading = this.userService.isLoading;
  protected hasSearched = this.userService.hasSearched;

  protected userStatuses:SelectItem[]=[];
  protected readonly searchType:SearchType = "search.type.users";
  protected tableColumns:SearchTableColumn[]=[];

  ngOnInit(): void {
    this.initForm();
    this.initTableColumns();
    this.initUserStatuses();
  }

  protected search():void{
    this.userService.executeSearchUsers(this.form);
  }

  protected resetForm():void{
    this.form.reset();
    this.userService.resetSearchResults();
  }

  protected handleTableLazyLoad(event: TableLazyLoadEvent): void{
    const {first,rows,sortField,sortOrder} = event;
    this.form.patchValue({first, rows,sortField,sortOrder});
    this.search();
  }

  protected handleLoadSavedSearch(event: SavedSearch):void{

  }

  private initForm():void{
    this.form = this.userService.initSearchUserForm();
  }

  private initUserStatuses():void{
    const translationPrefix: string = 'USER.STATUSES';
    this.userStatuses=[
      {
        label:this.translate.instant(`${translationPrefix}.active`),
        value:'account.active'
      },
      {
        label:this.translate.instant(`${translationPrefix}.inactive`),
        value:'account.inactive'
      },
      {
        label:this.translate.instant(`${translationPrefix}.deleted`),
        value:'account.deleted'
      }
    ];
  }

  private initTableColumns():void{
    this.tableColumns= this.userService.getSearchUserTableColumns();
  }

}
