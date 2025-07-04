import {ChangeDetectionStrategy, Component, computed, effect, inject, OnInit} from '@angular/core';
import {SignUpWithComponent} from '../../components/sign-up-with/sign-up-with.component';
import {TranslatePipe} from '@ngx-translate/core';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {ReactiveFormsModule} from '@angular/forms';
import {UserService} from '../../protected/user/data/services/user.service';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {InputText} from 'primeng/inputtext';
import {NgClass} from '@angular/common';
import {Password} from 'primeng/password';
import {Router, RouterLink} from '@angular/router';
import {ButtonDirective} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {UserRolesEnum} from '@models/constants';
import {Select} from 'primeng/select';

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
    Password,
    RouterLink,
    ButtonDirective,
    Ripple,
    Select
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
              @if (vm(); as vm) {
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
                    <label for="role"
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                           [ngClass]="{'app-required-label': form.enabled}">
                      {{ 'USER.DETAILS.LABELS.role' | translate }}
                    </label>
                    <p-select
                      formControlName="role"
                      [options]="vm.availableRoles"
                      [checkmark]="true"
                      [showClear]="true"
                      class="border-0 !bg-white text-sm shadow w-full"/>
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
                        styleClass="w-full"
                        formControlName="password"
                        [feedback]="true"
                        [toggleMask]="true" />
                    </app-form-control-wrap>
                    <app-form-error
                      [displayLabels]="isFieldValid('password')"
                      [validationErrors]="form.get('password')?.errors"/>
                  </div>
                  <div class="mt-4">
                    <label for="password"
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                           [ngClass]="{'app-required-label': form.enabled}">
                      {{ 'USER.DETAILS.LABELS.confirm-password' | translate }}
                    </label>
                    <app-form-control-wrap
                      [editMode]="form.enabled">
                      <p-password
                        id="confirmPassword"
                        inputStyleClass="border-0 !bg-white text-sm shadow w-full !text-black"
                        styleClass="w-full"
                        formControlName="confirmPassword"
                        [feedback]="true"
                        [toggleMask]="true" />
                    </app-form-control-wrap>
                    <app-form-error
                      [displayLabels]="isFieldValid('confirmPassword')"
                      [validationErrors]="form.get('confirmPassword')?.errors"/>
                  </div>

                  <div class="text-center mt-6">
                    <button
                      pButton
                      pRipple
                      severity="secondary"
                      class="font-bold uppercase px-6 py-3 rounded shadow mr-1 mb-1 w-full"
                      type="button"
                      (click)="registerUser()"
                      [loading]="vm.loading"
                      [disabled]="vm.loading">
                      {{ "REGISTER.BUTTONS.create-account" | translate }}
                    </button>
                  </div>
                </form>
              }
            </div>
          </div>
          <div class="flex flex-wrap mt-6 relative">
            <div class="w-1/2">
              <a [routerLink]="['/auth/login']" class="text-blueGray-200">
                <small>{{ "REGISTER.BUTTONS.already-have-account" | translate }}</small>
              </a>
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
  private userService = inject(UserService);
  private commonEntitiesService = inject(CommonEntitiesService);
  private router= inject(Router);

  constructor() {
    super();
    this.listenToSuccessfullUserRegistration();
  }

  ngOnInit(): void {
    this.initForm();
  }

  protected vm = computed(()=>{
    const loading = this.userService.isLoading();
    const availableRoles = this.commonEntitiesService.rolesAsSelectItems()
      .filter(r=>r.value!=UserRolesEnum.ROLE_SYSTEM_ADMIN);

    return {
      loading,
      availableRoles
    }
  });

  protected registerUser():void{
    this.userService.executeRegisterUser(this.form);
  }

  private initForm():void{
    this.form= this.userService.initCreateUserForm();
  }

  private listenToSuccessfullUserRegistration():void{
    effect(() => {
      const createdUserId = this.userService.createdUserId();
      if (createdUserId) {
        this.navigateToLogin();
      }
    });
  }

  private navigateToLogin():void{
    this.router.navigate(['/auth','login'], {
      queryParams: {},
    });
  }

}
