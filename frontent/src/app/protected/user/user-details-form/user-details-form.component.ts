import {ChangeDetectionStrategy, Component, inject, input} from '@angular/core';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {TranslatePipe} from '@ngx-translate/core';
import {SelectItem} from '@models/select-item.model';
import {BaseFormComponent} from '@shared/abstract/BaseFormComponent';
import {Field, FieldTree} from '@angular/forms/signals';
import {UpdateUserFormModel} from '../forms';
import {RolesConstants} from '@core/guards/SecurityConstants';
import {HlmInputImports} from '@components/ui/input';
import {HlmSelectImports} from '@components/ui/select';
import {UserService} from '../data';

@Component({
  selector: 'app-user-details-form',
  imports: [
    FormControlWrapComponent,
    FormErrorComponent,
    TranslatePipe,
    Field,
    HlmInputImports,
    HlmSelectImports,
  ],
  template: `
    @let form = formInput();
    <form>
      <div class="flex-auto px-4 lg:px-10 py-10 pt-0 ">
        <div class="grid gap-6 mb-6 md:grid-cols-2">
          <div class="mt-4">
            <label for="firstName"
                   [class.app-required-label]="!form().disabled()">
              {{ 'USER.DETAILS.LABELS.firstName' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()"
              [displayValue]="form.firstName().value()"
              [fetchingData]="fetchingData()">
              <input
                id="firstName"
                hlmInput
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
                   [class.app-required-label]="!form().disabled()">
              {{ 'USER.DETAILS.LABELS.lastName' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()"
              [displayValue]="form.lastName().value()"
              [fetchingData]="fetchingData()">
              <input
                id="lastName"
                hlmInput
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
                   [class.app-required-label]="!form().disabled()">
              {{ 'USER.DETAILS.LABELS.username' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()"
              [displayValue]="form.username().value()"
              [fetchingData]="fetchingData()">
              <input
                id="username"
                hlmInput
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
                   [class.app-required-label]="!form().disabled()">
              {{ 'USER.DETAILS.LABELS.email' | translate }}
            </label>
            <app-form-control-wrap
              [editMode]="!form().disabled()"
              [displayValue]="form.email().value()"
              [fetchingData]="fetchingData()">
              <input
                id="email"
                hlmInput
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
                   [class.app-required-label]="!form().disabled()">
              {{ 'USER.DETAILS.LABELS.role' | translate }}
            </label>
            <hlm-select
              [value]="form.role().value()"
              [itemToString]="roleItemToString"
              (valueChange)="handleRoleChange($event)"
              [disabled]="form().disabled()"
              class="border-0 !bg-white text-sm shadow w-full">
              <hlm-select-trigger class="w-full">
                <hlm-select-value />
              </hlm-select-trigger>
              <hlm-select-content *hlmSelectPortal>
                <hlm-select-group>
                  @for (role of availableRoles(); track role.value) {
                    <hlm-select-item [value]="role.value">{{ role.label }}</hlm-select-item>
                  }
                </hlm-select-group>
              </hlm-select-content>
            </hlm-select>
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

  private userService = inject(UserService);

  fetchingData = input<boolean>(false);
  formInput = input.required<FieldTree<UpdateUserFormModel, string | number>>();
  availableRoles = input.required<SelectItem[]>();

  protected handleRoleChange(role: RolesConstants | null | undefined): void {
    if (role) {
      this.formInput().role().value.set(role);
    }
  }

  protected roleItemToString = (value: RolesConstants): string =>
    this.userService.getSelectItemLabel(this.availableRoles(), value);

}
