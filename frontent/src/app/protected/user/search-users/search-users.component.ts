import {ChangeDetectionStrategy, Component, inject, OnInit, signal} from '@angular/core';
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
import {SavedSearch, SearchType} from '@models/search.model';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {UserService} from '../data/services/user.service';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {User} from '@models/user.model';
import {FieldsetModule} from 'primeng/fieldset';

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
  ],
  template: `
    <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0">
      <app-page-header>
        {{ 'USER.SEARCH.title' | translate }}
      </app-page-header>
      <app-required-fields-label></app-required-fields-label>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
        <div role="search">
          <p-fieldset legend="{{ 'SEARCH.COMMON.search-criteria' | translate }}"
            [toggleable]="true"
            [collapsed]="criteriaCollapsed()" >
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
          </p-fieldset>
        </div>
      </div>
    </div>
  `,
  styleUrl: './search-users.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchUsersComponent extends BaseComponent implements OnInit{
  protected fb= inject(FormBuilder);
  private userService = inject(UserService);
  protected commonEntitiesService = inject(CommonEntitiesService);

  protected results=signal<User[]>([]);
  protected criteriaCollapsed=this.userService.criteriaCollapsed;
  protected tableLoading=this.userService.tableLoading;
  protected loading = this.userService.isLoading;
  protected hasSearched = this.userService.hasSearched;

  protected userStatuses:SelectItem[]=[];
  protected readonly searchType:SearchType = "search.type.users";

  ngOnInit(): void {
    this.initForm();
    this.initUserStatuses();
  }

  protected search():void{
    this.userService.executeSearchUsers(this.form);
  }

  protected resetForm():void{
    this.form.reset();
    this.userService.resetSearchState();
    this.results.set([]);
  }

  protected handleTableLazyLoad(event: TableLazyLoadEvent): void{

  }

  protected handleLoadSavedSearch(event: SavedSearch):void{

  }

  private initForm():void{
    this.form = this.fb.group<UserSearchForm>({
      email: new FormControl(null),
      name: new FormControl(null),
      status: new FormControl("account.active",{nonNullable: true}),
      username: new FormControl(null),
      role: new FormControl(null),
      rows:new FormControl(10,{nonNullable: true}),
      first: new FormControl(0,{nonNullable: true}),
    });
  }

  private initUserStatuses():void{
    this.userStatuses=[
      {label:'Active',value:'account.active'},
      {label:'Inactive',value:'account.inactive'},
      {label:'Deleted',value:'account.deleted'}
    ];
  }

}
