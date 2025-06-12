import {ChangeDetectionStrategy, Component, computed, inject, OnInit, signal} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe} from '@ngx-translate/core';
import {UserService} from '../data/services/user.service';
import {USER_DETAILS_PROVIDERS, USERS_DETAILS} from './user-details.provider';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {FloatLabel} from 'primeng/floatlabel';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {NgClass} from '@angular/common';


@Component({
  selector: 'app-user-details',
  imports: [
    PageHeaderComponent,
    TranslatePipe,
    RequiredFieldsLabelComponent,
    FloatLabel,
    FormErrorComponent,
    FormsModule,
    InputText,
    ReactiveFormsModule,
    FormControlWrapComponent,
    NgClass,

  ],
  template: `
    <div
      class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0 text-black">
      <app-page-header>
        {{ 'USER.DETAILS.title' | translate }}
      </app-page-header>
      <app-required-fields-label/>
      @if (vm(); as vm) {
        @let user = vm.user;
        <form [formGroup]="form">
          <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
            <div class="grid gap-6 mb-6 md:grid-cols-2">
              <div class="mt-4">
                <label for="firstName"
                  [ngClass]="{'app-required-label': form.enabled}">
                  {{ 'USER.DETAILS.LABELS.firstName' | translate }}
                </label>
                <app-form-control-wrap
                  [editMode]="form.enabled"
                  [displayValue]="form.get('firstName')?.value"
                  [fetchingData]="vm.loading">
                  <input
                    id="firstName"
                    pInputText
                    type="text"
                    class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                    formControlName="firstName"
                    autocomplete="firstName"/>
                </app-form-control-wrap>
                <app-form-error
                  [displayLabels]="isFieldValid('firstName')"
                  [validationErrors]="form.get('firstName')?.errors"/>
              </div>
              <div class="mt-4">
                <label for="lastName"
                       [ngClass]="{'app-required-label': form.enabled}">
                  {{ 'USER.DETAILS.LABELS.lastName' | translate }}
                </label>
                <app-form-control-wrap
                  [editMode]="form.enabled"
                  [displayValue]="form.get('lastName')?.value"
                  [fetchingData]="vm.loading">
                  <input
                    id="lastName"
                    pInputText
                    type="text"
                    class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                    formControlName="lastName"
                    autocomplete="lastName"/>
                </app-form-control-wrap>
                <app-form-error
                  [displayLabels]="isFieldValid('lastName')"
                  [validationErrors]="form.get('lastName')?.errors"/>
              </div>
              <div class="mt-4">
                <label for="username"
                       [ngClass]="{'app-required-label': form.enabled}">
                  {{ 'USER.DETAILS.LABELS.username' | translate }}
                </label>
                <app-form-control-wrap
                  [editMode]="form.enabled"
                  [displayValue]="form.get('username')?.value"
                  [fetchingData]="vm.loading">
                  <input
                    id="username"
                    pInputText
                    type="text"
                    class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                    formControlName="username"
                    autocomplete="username"/>
                </app-form-control-wrap>
                <app-form-error
                  [displayLabels]="isFieldValid('username')"
                  [validationErrors]="form.get('username')?.errors"/>
              </div>
              <div class="mt-4">
                <label for="username"
                       [ngClass]="{'app-required-label': form.enabled}">
                  {{ 'USER.DETAILS.LABELS.email' | translate }}
                </label>
                <app-form-control-wrap
                  [editMode]="form.enabled"
                  [displayValue]="form.get('email')?.value"
                  [fetchingData]="vm.loading">
                  <input
                    id="username"
                    pInputText
                    type="email"
                    class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                    formControlName="email"
                    autocomplete="email"/>
                </app-form-control-wrap>
                <app-form-error
                  [displayLabels]="isFieldValid('email')"
                  [validationErrors]="form.get('email')?.errors"/>
              </div>
            </div>
          </div>
        </form>
      }
    </div>
  `,
  styleUrl: './user-details.component.css',
  providers:[
    USER_DETAILS_PROVIDERS
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent extends BaseComponent implements OnInit{
  private userService = inject(UserService);
  protected user = inject(USERS_DETAILS);
  protected loading = this.userService.isLoading;

  protected vm = computed(()=>{
    const loading = this.loading();
    const user = this.user();

    if(user){
      this.form.patchValue({
        username: user.username,
        firstName:user.firstName,
        lastName:user.lastName,
        email:user.email
      });
    }

    return {
      user,
      loading
    }
  });

  ngOnInit(): void {
    this.initForm();
    this.form.disable();
  }

  private initForm():void{
    this.form = this.userService.initUpdateUserForm();
  }

}
