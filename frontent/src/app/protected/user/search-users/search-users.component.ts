import {ChangeDetectionStrategy, Component, effect, inject, OnInit, signal, WritableSignal,} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {FloatLabel} from 'primeng/floatlabel';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {InputText} from 'primeng/inputtext';
import {TranslatePipe} from '@ngx-translate/core';
import {SelectItem} from 'primeng/api';
import {Select} from 'primeng/select';
import {SearchButtonsComponent} from '@components/search-buttons/search-buttons.component';
import {TableLazyLoadEvent} from 'primeng/table';
import {
  SavedSearch,
  SearchRequestCriteria,
  SearchTableColumn,
  SearchType, SearchTypeEnum,
  UserSearchRequest
} from '@models/search.model';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {UserService} from '../data/services/user.service';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {FieldsetModule} from 'primeng/fieldset';
import {ResultsTableComponent} from '@components/results-table/results-table.component';
import {ResultsTableFilterDirective} from '@directives/results-table-filter.directive';
import {ResultsTablePaginatorDirective} from '@directives/results-table-paginator.directive';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';
import {ReactiveFormsModule} from '@angular/forms';
import {NgClass} from "@angular/common";
import {Field, FieldTree, submit} from '@angular/forms/signals';
import {UserSearchFormModel} from '../forms';
import {RolesConstants} from '@core/guards/SecurityConstants';

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
    NgClass,
    Field,
  ],
  template: `
    <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0 text-black">
      <app-page-header>
        {{ 'USER.SEARCH.title' | translate }}
      </app-page-header>
      <app-required-fields-label/>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
        <div role="search">
          <app-fieldset legend="{{ 'SEARCH.COMMON.search-criteria' | translate }}"
                        [toggleable]="true"
                        [collapsed]="criteriaCollapsed()">
            <form>
              <div class="grid gap-6 mt-6 md:grid-cols-3">
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <input
                      id="email"
                      pInputText
                      type="email"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      [field]="form.email"
                      autocomplete="email"/>
                    <label for="email">{{ 'USER.SEARCH.LABELS.email' | translate }}</label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="form.email().invalid() && form.email().touched()"
                    [validationErrors]="form.email().errors()"/>
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <input
                      id="username"
                      pInputText
                      type="text"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      [field]="form.username"
                      autocomplete="username"/>
                    <label for="username">{{ 'USER.SEARCH.LABELS.username' | translate }}</label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="form.username().invalid() && form.username().touched()"
                    [validationErrors]="form.username().errors()"/>
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <input
                      id="name"
                      pInputText
                      type="text"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      [field]="form.name"
                      autocomplete="name"/>
                    <label for="name">{{ 'USER.SEARCH.LABELS.name' | translate }}</label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="form.name().invalid() && form.name().touched()"
                    [validationErrors]="form.name().errors()"/>
                </div>
              </div>
              <div class="grid gap-6 mt-6 md:grid-cols-2">
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <p-select
                      [field]="form.status"
                      [options]="userStatuses"
                      [checkmark]="true"
                      [showClear]="true"
                      class="border-0 !bg-white text-sm shadow w-full"/>
                    <label class="app-required-label" for="name">
                      {{ 'USER.SEARCH.LABELS.status' | translate }}
                    </label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="form.status().invalid() && form.status().touched()"
                    [validationErrors]="form.status().errors()"/>
                </div>
                <div class="mb-6">
                  <p-float-label variant="on" class="w-full mb-3">
                    <p-select
                      [field]="form.role"
                      [options]="commonEntitiesService.rolesAsSelectItems()"
                      [checkmark]="true"
                      [showClear]="true"
                      class="border-0 !bg-white text-sm shadow w-full"/>
                    <label class="app-required-label" for="name">
                      {{ 'USER.SEARCH.LABELS.role' | translate }}
                    </label>
                  </p-float-label>
                  <app-form-error
                    [displayLabels]="form.status().invalid() && form.status().touched()"
                    [validationErrors]="form.status().errors()"/>
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
          @if (resultsVisible()) {
            <div class="mt-6" [ngClass]="{'fade-in': hasSearched(), 'fade-out': !hasSearched()}">
              <app-results-table
                tableFilter
                tablePaginator
                [colTitles]="tableColumns"
                [tableItems]="results()"
                [totalRecords]="totalCount()"
                [resultsPerPage]="form.rows().value()"
                (tableStateChanged)="handleTableLazyLoad($event)"
                [first]="form.first().value()"
                [lazy]="true"
                [loading]="tableLoading()"
                [overrideDefaultExport]="true"
                [exportFunction]="exportReport.bind(this)"
              />
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styleUrl: './search-users.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchUsersComponent implements OnInit {
  private userService = inject(UserService);
  protected commonEntitiesService = inject(CommonEntitiesService);

  protected results = this.userService.searchResults;
  protected totalCount = this.userService.totalCount;
  protected criteriaCollapsed = this.userService.criteriaCollapsed;
  protected tableLoading = this.userService.tableLoading;
  protected loading = this.userService.isLoading;
  protected hasSearched = this.userService.hasSearched;

  protected userStatuses: SelectItem[] = [];
  protected readonly searchType: SearchType = SearchTypeEnum.USERS;
  protected tableColumns: SearchTableColumn[] = [];
  protected resultsVisible: WritableSignal<boolean> = signal(false);
  protected animationTimer: any;

  form!: FieldTree<UserSearchFormModel, string | number>;

  constructor() {
    this.initForm();
    effect(() => {
      clearTimeout(this.animationTimer);
      if (this.hasSearched()) {
        this.resultsVisible.set(true);
      } else {
        this.animationTimer = setTimeout(() => {
          this.resultsVisible.set(false);
        }, 300); // Must match the animation duration in CSS
      }
    });
  }

  ngOnInit(): void {
    this.initTableColumns();
    this.initUserStatuses();
  }

  protected search(): void {
    submit(this.form,async (form)=>{
      this.userService.executeSearchUsers(form);
    })

  }

  protected resetForm(): void {
    this.form().reset();
    this.userService.resetSearchResults();
  }

  protected exportReport(): void {
    this.userService.exportUsersToCsv(this.form);
  }

  protected handleTableLazyLoad(event: TableLazyLoadEvent): void {
    const {first, rows, sortField, sortOrder} = event;
    this.form.first().value.set(first??0);
    this.form.rows().value.set(rows??10);
    this.form.sortField().value.set(sortField as string);
    this.form.sortOrder().value.set(sortOrder == 1 ? "ASC" : "DESC");
    this.search();
  }

  protected handleLoadSavedSearch(selectedSavedSearch: SavedSearch): void {
    const {criteria} = selectedSavedSearch;
    this.loadSavedSearch(criteria);
  }

  private loadSavedSearch(searchRequest: SearchRequestCriteria): void {
    const {email, name, status, roleName, username} = searchRequest as UserSearchRequest;
    this.form.email().value.set(email??'');
    this.form.status().value.set(status);
    this.form.role().value.set(roleName as RolesConstants);
    this.form.username().value.set(username??'');
    this.form.name().value.set(name??'');
    this.search();
  }

  private initForm(): void {
    this.form = this.userService.initSearchUserForm();
  }

  private initUserStatuses(): void {
    this.userStatuses = this.userService.initUserStatuses();
  }

  private initTableColumns(): void {
    this.tableColumns = this.userService.getSearchUserTableColumns();
  }

}
