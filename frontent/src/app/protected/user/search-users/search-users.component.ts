import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {FormBuilder, FormControl, ReactiveFormsModule} from '@angular/forms';
import {UserSearchForm} from '../forms';
import {UserAccountStatus} from '@models/user.model';
import {FloatLabel} from 'primeng/floatlabel';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {InputText} from 'primeng/inputtext';
import {TranslatePipe} from '@ngx-translate/core';
import {SelectItem} from 'primeng/api';
import {Select} from 'primeng/select';

@Component({
  selector: 'app-search-users',
  imports: [
    PageHeaderComponent,
    ReactiveFormsModule,
    FloatLabel,
    FormErrorComponent,
    InputText,
    TranslatePipe,
    Select
  ],
  template: `
    <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0">
      <app-page-header>
        {{ 'USER.SEARCH.title' | translate }}
      </app-page-header>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
        <div role="search">
          <form [formGroup]="form">
            <div class="grid gap-6 mb-6 md:grid-cols-2 mt-4">
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
              <div class="mb-6">
                <p-float-label variant="on" class="w-full mb-3">
                  <p-select
                    formControlName="status"
                    [options]="userStatuses"
                    [checkmark]="true"
                    [showClear]="true"
                    class="border-0 !bg-white text-sm shadow w-full"/>
                  <label for="name">{{ 'USER.SEARCH.LABELS.status' | translate }}</label>
                </p-float-label>
                <app-form-error
                  [displayLabels]="isFieldValid('status')"
                  [validationErrors]="form.get('status')?.errors" />
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styleUrl: './search-users.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchUsersComponent extends BaseComponent implements OnInit{
  private fb= inject(FormBuilder);

  protected userRoles:SelectItem[]=[];
  protected userStatuses:SelectItem[]=[];

  ngOnInit(): void {
    this.initForm();
    this.initUserRoles();
    this.initUserStatuses();
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

  private initUserRoles():void{
    // this.userRoles=[
    //   {label:''}
    // ];
  }

  private initUserStatuses():void{
    this.userStatuses=[
      {label:'Active',value:'account.active'},
      {label:'Inactive',value:'account.inactive'},
      {label:'Deleted',value:'account.deleted'}
    ];
  }

}
