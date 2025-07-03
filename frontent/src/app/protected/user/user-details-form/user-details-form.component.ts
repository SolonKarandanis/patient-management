import {ChangeDetectionStrategy, Component, inject, input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {InputText} from 'primeng/inputtext';
import {TranslatePipe} from '@ngx-translate/core';
import {NgClass} from '@angular/common';
import {Select} from 'primeng/select';
import {SelectItem} from 'primeng/api';
import {BaseFormComponent} from '@shared/abstract/BaseFormComponent';

@Component({
  selector: 'app-user-details-form',
  imports: [
    FormControlWrapComponent,
    FormErrorComponent,
    InputText,
    ReactiveFormsModule,
    TranslatePipe,
    NgClass,
    Select
  ],
  template: `
    @let form = formGroup();
    <form [formGroup]="form">
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0 ">
        <div class="grid gap-6 mb-6 md:grid-cols-2">
          <div class="mt-4">
            <label for="firstName"
                   [ngClass]="{'app-required-label': form.enabled}">
              {{ 'USER.DETAILS.LABELS.firstName' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="form.enabled"
              [displayValue]="form.get('firstName')?.value"
              [fetchingData]="fetchingData()">
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
              [fetchingData]="fetchingData()">
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
              [fetchingData]="fetchingData()">
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
            <label for="email"
                   [ngClass]="{'app-required-label': form.enabled}">
              {{ 'USER.DETAILS.LABELS.email' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="form.enabled"
              [displayValue]="form.get('email')?.value"
              [fetchingData]="fetchingData()">
              <input
                id="email"
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
          <div class="mt-4">
            <label for="role"
                   [ngClass]="{'app-required-label': form.enabled}">
              {{ 'USER.DETAILS.LABELS.role' | translate }}
            </label>
            <p-select
              formControlName="role"
              [options]="availableRoles()"
              [checkmark]="true"
              [showClear]="true"
              class="border-0 !bg-white text-sm shadow w-full"/>
            <app-form-error
              [displayLabels]="isFieldValid('role')"
              [validationErrors]="form.get('role')?.errors"/>
          </div>
        </div>
      </div>
    </form>
  `,
  styleUrl: './user-details-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsFormComponent  extends BaseFormComponent{

  fetchingData = input<boolean>(false);
  formGroup = input.required<FormGroup>();
  availableRoles = input.required<SelectItem[]>();

  protected isFieldValid(field: string): boolean | undefined {
    return this.isFieldValidBase(field, this.formGroup());
  }
}
