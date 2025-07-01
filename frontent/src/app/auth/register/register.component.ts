import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {SignUpWithComponent} from '../../components/sign-up-with/sign-up-with.component';
import {TranslatePipe} from '@ngx-translate/core';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {UserService} from '../../protected/user/data/services/user.service';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {InputText} from 'primeng/inputtext';
import {NgClass} from '@angular/common';
import {Password} from 'primeng/password';

@Component({
  selector: 'app-register',
  imports: [
    SignUpWithComponent,
    TranslatePipe,
    ReactiveFormsModule,
    FormControlWrapComponent,
    FormErrorComponent,
    InputText,
    NgClass,
    Password
  ],
  template: `
    <div class="container mx-auto px-4 h-full">
      <div class="flex content-center items-center justify-center h-full">
        <div class="w-full lg:w-6/12 px-4">
          <div
            class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg
            rounded-lg bg-blueGray-200 border-0">
            <app-sign-up-with></app-sign-up-with>
            <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
              <div class="text-blueGray-400 text-center mb-3 font-bold">
                <small>{{ 'GLOBAL.sign-in-with-credentials' | translate }}</small>
              </div>
              <form [formGroup]="form">
                <div class="mt-4">
                  <label for="email"
                         class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                         [ngClass]="{'app-required-label': form.enabled}">
                    {{ 'USER.DETAILS.LABELS.email' | translate }}
                  </label>
                  <app-form-control-wrap
                    [editMode]="form.enabled"
                    [displayValue]="form.get('email')?.value">
                    <input
                      id="email"
                      pInputText
                      type="text"
                      class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                      formControlName="email"
                      autocomplete="email"/>
                  </app-form-control-wrap>
                  <app-form-error
                    [displayLabels]="isFieldValid('email')"
                    [validationErrors]="form.get('email')?.errors"/>
                </div>
                <div class="mt-4">
                  <label for="username"
                         class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                         [ngClass]="{'app-required-label': form.enabled}">
                    {{ 'USER.DETAILS.LABELS.username' | translate }}
                  </label>
                  <app-form-control-wrap
                    [editMode]="form.enabled"
                    [displayValue]="form.get('username')?.value">
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
                  <label for="firstName"
                         class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                         [ngClass]="{'app-required-label': form.enabled}">
                    {{ 'USER.DETAILS.LABELS.firstName' | translate }}
                  </label>
                  <app-form-control-wrap
                    [editMode]="form.enabled"
                    [displayValue]="form.get('firstName')?.value">
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
                         class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                         [ngClass]="{'app-required-label': form.enabled}">
                    {{ 'USER.DETAILS.LABELS.lastName' | translate }}
                  </label>
                  <app-form-control-wrap
                    [editMode]="form.enabled"
                    [displayValue]="form.get('lastName')?.value">
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
                  <label for="password"
                         class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                         [ngClass]="{'app-required-label': form.enabled}">
                    {{ 'USER.DETAILS.LABELS.password' | translate }}
                  </label>
                  <app-form-control-wrap
                    [editMode]="form.enabled">
                    <p-password
                      id="password"
                      inputStyleClass="border-0 !bg-white text-sm shadow w-full !text-black"
                      formControlName="password"
                      [feedback]="true"
                      [toggleMask]="true" />
                  </app-form-control-wrap>
                  <app-form-error
                    [displayLabels]="isFieldValid('password')"
                    [validationErrors]="form.get('password')?.errors"/>
                </div>
                <div class="text-center mt-6">
                  <button
                    class="bg-blueGray-800 text-white active:bg-blueGray-600 text-sm font-bold uppercase px-6 py-3
                    rounded shadow hover:shadow-lg outline-none focus:outline-none mr-1 mb-1 w-full ease-linear transition-all duration-150"
                    type="button"
                  >
                    Create Account
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrl: './register.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegisterComponent extends BaseComponent implements OnInit{
  private fb= inject(FormBuilder);
  private userService = inject(UserService);

  ngOnInit(): void {
    this.initForm();
  }

  private initForm():void{
    this.form= this.userService.initCreateUserForm();
  }

}
