import {ChangeDetectionStrategy, Component, input, signal} from '@angular/core';
import {TranslatePipe} from '@ngx-translate/core';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {BaseFormComponent} from '@shared/abstract/BaseFormComponent';
import {Field, FieldTree} from '@angular/forms/signals';
import {ChangePasswordFormModel} from '../forms';
import {HlmInputImports} from '@components/ui/input';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideEye, lucideEyeOff} from '@ng-icons/lucide';

@Component({
  selector: 'app-user-password-change-form',
  imports: [
    TranslatePipe,
    FormControlWrapComponent,
    FormErrorComponent,
    Field,
    HlmInputImports,
    NgIcon,
  ],
  providers: [provideIcons({lucideEye, lucideEyeOff})],
  template: `
    @let form = formInput();
    <form>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0 ">
        <div class="grid gap-6 mb-6 md:grid-cols-2">
          <div class="mt-4">
            <label for="password"
                   [class.app-required-label]="!form().disabled()">
              {{ 'USER.DETAILS.LABELS.password' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()">
              <div class="relative">
                <input
                  id="password"
                  hlmInput
                  [type]="showPassword() ? 'text' : 'password'"
                  class="border-0 px-3 py-3 pr-10 !bg-white text-sm shadow w-full !text-black"
                  [field]="form.password"
                  autocomplete="new-password"/>
                <button
                  type="button"
                  class="absolute inset-y-0 right-2 flex items-center text-blueGray-600"
                  (click)="showPassword.set(!showPassword())"
                  [attr.aria-label]="showPassword() ? 'Hide password' : 'Show password'">
                  <ng-icon [name]="showPassword() ? 'lucideEyeOff' : 'lucideEye'" />
                </button>
              </div>
            </app-form-control-wrap>
            <app-form-error
              [displayLabels]="form.password().invalid() && form.password().touched()"
              [validationErrors]="form.password().errors()"
              validationErrorsTranslationPrefix="REGISTER.MESSAGES.ERROR."/>
          </div>
          <div class="mt-4">
            <label for="confirmPassword"
                   [class.app-required-label]="!form().disabled()">
              {{ 'USER.DETAILS.LABELS.confirm-password' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()">
              <div class="relative">
                <input
                  id="confirmPassword"
                  hlmInput
                  [type]="showConfirmPassword() ? 'text' : 'password'"
                  class="border-0 px-3 py-3 pr-10 !bg-white text-sm shadow w-full !text-black"
                  [field]="form.confirmPassword"
                  autocomplete="new-password"/>
                <button
                  type="button"
                  class="absolute inset-y-0 right-2 flex items-center text-blueGray-600"
                  (click)="showConfirmPassword.set(!showConfirmPassword())"
                  [attr.aria-label]="showConfirmPassword() ? 'Hide password' : 'Show password'">
                  <ng-icon [name]="showConfirmPassword() ? 'lucideEyeOff' : 'lucideEye'" />
                </button>
              </div>
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

  protected showPassword = signal(false);
  protected showConfirmPassword = signal(false);
}
