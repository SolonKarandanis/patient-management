import {ChangeDetectionStrategy, Component, computed, effect, inject, signal,} from '@angular/core';
import {SignUpWithComponent} from '../../components/sign-up-with/sign-up-with.component';
import {TranslatePipe} from '@ngx-translate/core';
import {ReactiveFormsModule} from '@angular/forms';
import {UserService, CreateUserFormModel} from '../../protected/user';
import {FormControlWrapComponent} from '@components/form-control-wrap/form-control-wrap.component';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {Router, RouterLink} from '@angular/router';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {UserRolesEnum} from '@models/constants';
import {Field, FieldTree, submit} from '@angular/forms/signals';
import {SelectItem} from '@models/select-item.model';
import {HlmInputImports} from '@components/ui/input';
import {HlmButtonImports} from '@components/ui/button';
import {HlmSpinnerImports} from '@components/ui/spinner';
import {HlmSelectImports} from '@components/ui/select';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideEye, lucideEyeOff} from '@ng-icons/lucide';

@Component({
  selector: 'app-register',
  imports: [
    SignUpWithComponent,
    TranslatePipe,
    ReactiveFormsModule,
    FormControlWrapComponent,
    FormErrorComponent,
    RouterLink,
    Field,
    HlmInputImports,
    HlmButtonImports,
    HlmSpinnerImports,
    HlmSelectImports,
    NgIcon,
  ],
  providers: [provideIcons({lucideEye, lucideEyeOff})],
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
                <form>
                  <div class="mt-4">
                    <label for="email"
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                           [class.app-required-label]="!form().disabled()">
                      {{ 'USER.DETAILS.LABELS.email' | translate }}
                    </label>
                    <app-form-control-wrap
                      [editMode]="!form().disabled()"
                      [displayValue]="form.email().value()">
                      <input
                        id="email"
                        hlmInput
                        type="text"
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
                    <label for="username"
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                           [class.app-required-label]="!form().disabled()">
                      {{ 'USER.DETAILS.LABELS.username' | translate }}
                    </label>
                    <app-form-control-wrap
                      [editMode]="!form().disabled()"
                      [displayValue]="form.username().value()">
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
                    <label for="firstName"
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                           [class.app-required-label]="!form().disabled()">
                      {{ 'USER.DETAILS.LABELS.firstName' | translate }}
                    </label>
                    <app-form-control-wrap
                      [editMode]="!form().disabled()"
                      [displayValue]="form.firstName().value()">
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
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                           [class.app-required-label]="!form().disabled()">
                      {{ 'USER.DETAILS.LABELS.lastName' | translate }}
                    </label>
                    <app-form-control-wrap
                      [editMode]="!form().disabled()"
                      [displayValue]="form.lastName().value()">
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
                    <label for="role"
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                           [class.app-required-label]="!form().disabled()">
                      {{ 'USER.DETAILS.LABELS.role' | translate }}
                    </label>
                    <hlm-select
                      [value]="form.role().value()"
                      (valueChange)="form.role().value.set($event ?? null)"
                      class="border-0 !bg-white text-sm shadow w-full">
                      <hlm-select-trigger class="w-full">
                        <hlm-select-value />
                      </hlm-select-trigger>
                      <hlm-select-content *hlmSelectPortal>
                        <hlm-select-group>
                          @for (role of vm.availableRoles; track role.value) {
                            <hlm-select-item [value]="role.value">{{ role.label }}</hlm-select-item>
                          }
                        </hlm-select-group>
                      </hlm-select-content>
                    </hlm-select>
                  </div>
                  <div class="mt-4">
                    <label for="password"
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
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
                           class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
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

                  <div class="text-center mt-6">
                    <button
                      hlmBtn
                      variant="secondary"
                      class="font-bold uppercase px-6 py-3 rounded shadow mr-1 mb-1 w-full"
                      type="button"
                      (click)="registerUser()"
                      [disabled]="vm.loading">
                      @if (vm.loading) {
                        <hlm-spinner class="mr-2" />
                      }
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
export class RegisterComponent{
  private userService = inject(UserService);
  private commonEntitiesService = inject(CommonEntitiesService);
  private router= inject(Router);

  form!: FieldTree<CreateUserFormModel, string | number>;

  protected showPassword = signal(false);
  protected showConfirmPassword = signal(false);

  constructor() {
    this.initForm();
    this.listenToSuccessfullUserRegistration();
  }

  protected vm = computed(()=> buildRegisterViewModel(
    this.userService.isDetailLoading(),
    this.commonEntitiesService.rolesAsSelectItems(),
  ));

  protected registerUser():void{
    if (this.form().invalid()) {
      this.userService.markCreateUserFormAsDirty(this.form);
      return;
    }
    submit(this.form,async (form)=>{
      this.userService.executeRegisterUser(form);
    })

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

function buildRegisterViewModel(loading: boolean, allRoles: SelectItem[]) {
  const availableRoles = allRoles.filter((r) => r.value != UserRolesEnum.ROLE_SYSTEM_ADMIN);
  return { loading, availableRoles };
}
