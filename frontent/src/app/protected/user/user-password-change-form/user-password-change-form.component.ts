import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {TranslatePipe} from '@ngx-translate/core';
import {NgClass} from '@angular/common';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {Password} from 'primeng/password';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {BaseFormComponent} from '@shared/abstract/BaseFormComponent';
import {Field, FieldTree} from '@angular/forms/signals';
import {ChangePasswordFormModel} from '../forms';

@Component({
  selector: 'app-user-password-change-form',
  imports: [
    TranslatePipe,
    NgClass,
    FormControlWrapComponent,
    Password,
    FormErrorComponent,
    Field
  ],
  template: `
    @let form = formInput();
    <form>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0 ">
        <div class="grid gap-6 mb-6 md:grid-cols-2">
          <div class="mt-4">
            <label for="password"
                   [ngClass]="{'app-required-label':  !form().disabled()}">
              {{ 'USER.DETAILS.LABELS.password' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()">
              <p-password
                id="password"
                inputStyleClass="border-0 !bg-white text-sm shadow w-full !text-black"
                styleClass="w-full"
                [field]="form.password"
                [feedback]="true"
                [toggleMask]="true" />
            </app-form-control-wrap>
            <app-form-error
              [displayLabels]="form.password().invalid() && form.password().touched()"
              [validationErrors]="form.password().errors()"
              validationErrorsTranslationPrefix="REGISTER.MESSAGES.ERROR."/>
          </div>
          <div class="mt-4">
            <label for="password"
                   [ngClass]="{'app-required-label':  !form().disabled()}">
              {{ 'USER.DETAILS.LABELS.confirm-password' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()">
              <p-password
                id="confirmPassword"
                inputStyleClass="border-0 !bg-white text-sm shadow w-full !text-black"
                styleClass="w-full"
                [field]="form.confirmPassword"
                [feedback]="true"
                [toggleMask]="true" />
            </app-form-control-wrap>
            <app-form-error
              [displayLabels]="form.confirmPassword().invalid() && form.confirmPassword().touched()"
              [validationErrors]="form.confirmPassword().errors()"
              validationErrorsTranslationPrefix="REGISTER.MESSAGES.ERROR."/>
          </div>
        </div>
      </div>
    </form>
  `,
  styleUrl: './user-password-change-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPasswordChangeFormComponent extends BaseFormComponent{
  formInput = input.required<FieldTree<ChangePasswordFormModel, string | number>>();
}
