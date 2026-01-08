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
import {Field, FieldTree} from '@angular/forms/signals';
import {UpdateUserFormModel} from '../forms';
import {Password} from 'primeng/password';

@Component({
  selector: 'app-user-details-form',
  imports: [
    FormControlWrapComponent,
    FormErrorComponent,
    InputText,
    ReactiveFormsModule,
    TranslatePipe,
    NgClass,
    Select,
    Password,
    Field
  ],
  template: `
    @let form = formInput();
    <form>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0 ">
        <div class="grid gap-6 mb-6 md:grid-cols-2">
          <div class="mt-4">
            <label for="firstName"
                   [ngClass]="{'app-required-label': !form().disabled()}">
              {{ 'USER.DETAILS.LABELS.firstName' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()"
              [displayValue]="form.firstName().value()"
              [fetchingData]="fetchingData()">
              <input
                id="firstName"
                pInputText
                type="text"
                class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                [field]="form.firstName"
                autocomplete="firstName"/>
            </app-form-control-wrap>
            <app-form-error
              [displayLabels]="form.firstName().invalid() && form.firstName().touched()"
              [validationErrors]="form.firstName().errors()"
              validationErrorsTranslationPrefix="REGISTER.MESSAGES.ERROR."/>
          </div>
          <div class="mt-4">
            <label for="lastName"
                   [ngClass]="{'app-required-label': !form().disabled()}">
              {{ 'USER.DETAILS.LABELS.lastName' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()"
              [displayValue]="form.lastName().value()"
              [fetchingData]="fetchingData()">
              <input
                id="lastName"
                pInputText
                type="text"
                class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                [field]="form.lastName"
                autocomplete="lastName"/>
            </app-form-control-wrap>
            <app-form-error
              [displayLabels]="form.lastName().invalid() && form.lastName().touched()"
              [validationErrors]="form.lastName().errors()"
              validationErrorsTranslationPrefix="REGISTER.MESSAGES.ERROR."/>
          </div>
          <div class="mt-4">
            <label for="username"
                   [ngClass]="{'app-required-label': !form().disabled()}">
              {{ 'USER.DETAILS.LABELS.username' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()"
              [displayValue]="form.username().value()"
              [fetchingData]="fetchingData()">
              <input
                id="username"
                pInputText
                type="text"
                class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                [field]="form.username"
                autocomplete="username"/>
            </app-form-control-wrap>
            <app-form-error
              [displayLabels]="form.username().invalid() && form.username().touched()"
              [validationErrors]="form.username().errors()"
              validationErrorsTranslationPrefix="REGISTER.MESSAGES.ERROR."/>
          </div>
          <div class="mt-4">
            <label for="email"
                   [ngClass]="{'app-required-label':!form().disabled()}">
              {{ 'USER.DETAILS.LABELS.email' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()"
              [displayValue]="form.email().value()"
              [fetchingData]="fetchingData()">
              <input
                id="email"
                pInputText
                type="email"
                class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                [field]="form.email"
                autocomplete="email"/>
            </app-form-control-wrap>
            <app-form-error
              [displayLabels]="form.email().invalid() && form.email().touched()"
              [validationErrors]="form.email().errors()"
              validationErrorsTranslationPrefix="REGISTER.MESSAGES.ERROR."/>
          </div>
          <div class="mt-4">
            <label for="role"
                   [ngClass]="{'app-required-label': !form().disabled()}">
              {{ 'USER.DETAILS.LABELS.role' | translate }}
            </label>
            <p-select
              [field]="form.role"
              [options]="availableRoles()"
              [checkmark]="true"
              [showClear]="true"
              class="border-0 !bg-white text-sm shadow w-full"/>
            <app-form-error
              [displayLabels]="form.role().invalid() && form.role().touched()"
              [validationErrors]="form.role().errors()"
              validationErrorsTranslationPrefix="REGISTER.MESSAGES.ERROR."/>
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
  formInput = input.required<FieldTree<UpdateUserFormModel, string | number>>();
  availableRoles = input.required<SelectItem[]>();

}
