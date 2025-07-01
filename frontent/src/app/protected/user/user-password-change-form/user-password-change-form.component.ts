import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {NgClass} from '@angular/common';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {Password} from 'primeng/password';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {BaseFormComponent} from '@shared/abstract/BaseFormComponent';

@Component({
  selector: 'app-user-password-change-form',
  imports: [
    TranslatePipe,
    ReactiveFormsModule,
    NgClass,
    FormControlWrapComponent,
    Password,
    FormErrorComponent
  ],
  template: `
    @let form = formGroup();
    <form [formGroup]="form">
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0 ">
        <div class="grid gap-6 mb-6 md:grid-cols-2">
          <div class="mt-4">
            <label for="password"
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
        </div>
      </div>
    </form>
  `,
  styleUrl: './user-password-change-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPasswordChangeFormComponent extends BaseFormComponent{

  formGroup = input.required<FormGroup>();

  protected isFieldValid(field: string): boolean | undefined {
    return this.isFieldValidBase(field, this.formGroup());
  }

}
