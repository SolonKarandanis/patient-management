import {ChangeDetectionStrategy, Component, computed, effect, inject} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {UserService} from '../data';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {UserDetailsFormComponent} from '../user-details-form/user-details-form.component';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {AuthService} from '@core/services/auth.service';
import {UserRolesEnum} from '@models/constants';
import {UserPasswordChangeFormComponent} from '../user-password-change-form/user-password-change-form.component';
import {UtilService} from '@core/services/util.service';
import {SelectItem} from '@models/select-item.model';
import {User, UserAccountStatusEnum} from '@models/user.model';
import {FieldTree} from '@angular/forms/signals';
import {ChangePasswordFormModel, UpdateUserFormModel} from '../forms';
import {HlmButtonImports} from '@components/ui/button';
import {HlmDropdownMenuImports} from '@components/ui/dropdown-menu';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideBan, lucideCheck, lucideSettings, lucideTrash2} from '@ng-icons/lucide';



@Component({
  selector: 'app-user-details',
  imports: [
    PageHeaderComponent,
    TranslatePipe,
    RequiredFieldsLabelComponent,
    UserDetailsFormComponent,
    FieldsetComponent,
    UserPasswordChangeFormComponent,
    HlmButtonImports,
    HlmDropdownMenuImports,
    NgIcon,
  ],
  providers: [provideIcons({lucideBan, lucideCheck, lucideSettings, lucideTrash2})],
  template: `
    <div
      class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0 text-black">
      <app-page-header>
        {{ 'USER.DETAILS.title' | translate }}
      </app-page-header>
      <app-required-fields-label/>
      @if (vm(); as vm) {
        @if(vm.user){
          <app-fieldset
            legend="{{ 'USER.DETAILS.LABELS.details' | translate }}"
            [toggleable]="false"
            [allowEdit]="vm.isEditAllowed"
            [allowSave]="form().valid()"
            (saveClicked)="detailsSaveClickHandler()"
            (validateFormClicked)="detailsSaveFormValidateHandler()"
            (resetFormValidityClicked)="detailsSaveFormResetValidationHandler()"
            (editModeChanged)="detailsEditHandler($event)">
            <app-user-details-form
              [formInput]="form"
              [fetchingData]="vm.loading"
              [availableRoles]="vm.availableRoles"/>
          </app-fieldset>
          <app-fieldset
            legend="{{ 'USER.DETAILS.LABELS.change-password' | translate }}"
            [toggleable]="false"
            [allowEdit]="vm.isEditAllowed"
            [allowSave]="changePasswordForm().valid()"
            (saveClicked)="changePasswordSaveClickHandler()"
            (validateFormClicked)="changePasswordFormValidateHandler()"
            (resetFormValidityClicked)="changePasswordFormResetValidationHandler()"
            (editModeChanged)="changePasswordEditHandler($event)">
            <app-user-password-change-form [formInput]="changePasswordForm" />
          </app-fieldset>
          <app-fieldset
            legend="{{ 'USER.DETAILS.LABELS.account-status' | translate }}"
            [toggleable]="false"
            [allowEdit]="false">
            <div class="flex justify-between">
              <p class="py-2 text-xl font-semibold">{{ 'USER.DETAILS.LABELS.manage-account-status' | translate }}</p>
              <button
                hlmBtn
                variant="outline"
                type="button"
                [disabled]="!vm.isEditAllowed"
                [hlmDropdownMenuTrigger]="accountActionsMenu">
                {{ 'USER.DETAILS.LABELS.account-actions' | translate }}
                <ng-icon name="lucideSettings" />
              </button>
              <ng-template #accountActionsMenu>
                <div hlmDropdownMenu>
                  @for (action of accountActions; track action.label) {
                    <button hlmDropdownMenuItem [disabled]="action.disabled" (triggered)="action.command()">
                      <ng-icon [name]="action.icon" />
                      {{ action.label }}
                    </button>
                  }
                </div>
              </ng-template>
            </div>
          </app-fieldset>
        }
      }
    </div>
  `,
  styleUrl: './user-details.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent  {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private commonEntitiesService = inject(CommonEntitiesService);
  private translate = inject(TranslateService);
  private utilService = inject(UtilService);
  protected changePasswordForm!: FieldTree<ChangePasswordFormModel, string | number>;
  protected accountActions!: AccountAction[];

  protected form: FieldTree<UpdateUserFormModel, string | number>;

  constructor() {
    this.form = this.userService.userUpdateForm;

    effect(() => {
      const user = this.userService.user();
      if (user) {
        const userRoles = this.userService.rolesAsSelectItems();
        this.initChangePasswordForm();
        this.initMenuActions(user.status);
        this.userService.updateUserDetailsForm({
          username: user.username,
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          role: userRoles[0]?.value,
        });
        this.userService.setUpdateFormDisabled(true);

      }
    });
  }

  protected vm = computed(()=> buildUserDetailsViewModel(
    this.userService.user(),
    this.commonEntitiesService.rolesAsSelectItems(),
    this.userService.rolesAsSelectItems(),
    this.authService.hasRole(UserRolesEnum.ROLE_SYSTEM_ADMIN)(),
    this.authService.isUserMe(this.userService.user()?.publicId)(),
    this.userService.isDetailLoading(),
  ));

  private initChangePasswordForm():void{
    this.changePasswordForm = this.userService.changePasswordForm;
    this.userService.setChangePasswordFormDisabled(true);
  }


  protected detailsSaveFormValidateHandler():void{
    if(this.form().invalid()){
      this.userService.markUpdateUserFormAsDirty(this.form);
    }
  }

  protected detailsSaveFormResetValidationHandler():void{
    this.userService.markUpdateUserFormAsPristine(this.form)
  }

  protected detailsSaveClickHandler():void{
    if(this.form().valid()){
      this.userService.executeUpdateUser(this.form);
    }
  }

  protected detailsEditHandler(isEditMode: boolean):void{
    this.userService.setUpdateFormDisabled(!isEditMode);
  }

  protected changePasswordFormValidateHandler():void{
    if(!this.changePasswordForm().invalid()){
      this.userService.markChangePasswordFormAsDirty(this.changePasswordForm);
    }
  }

  protected changePasswordFormResetValidationHandler():void{
    this.userService.markChangePasswordFormAsPristine(this.changePasswordForm);
  }

  protected changePasswordSaveClickHandler():void{
    if(this.changePasswordForm().valid()){
      this.userService.executeChangeUserPassword(this.changePasswordForm);
    }
  }

  protected changePasswordEditHandler(isEditMode: boolean):void{
    this.userService.setChangePasswordFormDisabled(!isEditMode);
  }

  private getTranslationPrefix():string{
    return 'USER.DIALOGS.STATUS-CONFIRMATION.LABELS'
  }

  private handleUserActivation():void{
    const header = this.translate.instant(`${this.getTranslationPrefix()}.activation-header`);
    const message = this.translate.instant(`${this.getTranslationPrefix()}.activate-user`);
    this.utilService.showConfirmation({
      header,
      message,
      accept:()=>{
        this.performUserActivation();
      }
    });
  }

  private performUserActivation():void{
    this.userService.executeActivateUser();
  }

  private handleUserDeActivation():void{
    const header = this.translate.instant(`${this.getTranslationPrefix()}.deactivation-header`);
    const message = this.translate.instant(`${this.getTranslationPrefix()}.deactivate-user`);
    this.utilService.showConfirmation({
      header,
      message,
      accept:()=>{
        this.performUserDeActivation();
      }
    });
  }

  private performUserDeActivation():void{
    this.userService.executeDeactivateUser();
  }

  private handleUserDeletion():void{
    const header = this.translate.instant(`${this.getTranslationPrefix()}.deletion-header`);
    const message = this.translate.instant(`${this.getTranslationPrefix()}.delete-user`);
    this.utilService.showConfirmation({
      header,
      message,
      accept:()=>{
        this.performUserDeletion();
      }
    });
  }

  private performUserDeletion():void{
    this.userService.executeDeleteUser();
  }

  private initMenuActions(status:string):void{
    const translationPrefix: string = 'USER.DETAILS.LABELS';
    this.accountActions=[
      {
        label: this.translate.instant(`${translationPrefix}.activate`),
        icon: 'lucideCheck',
        disabled: status ===UserAccountStatusEnum.ACTIVE,
        command: () => {
          this.handleUserActivation();
        },
      },
      {
        label: this.translate.instant(`${translationPrefix}.deactivate`),
        icon: 'lucideBan',
        disabled: status ===UserAccountStatusEnum.INACTIVE,
        command: () => {
          this.handleUserDeActivation();
        },
      },
      {
        label: this.translate.instant(`${translationPrefix}.delete`),
        icon: 'lucideTrash2',
        disabled: status ===UserAccountStatusEnum.DELETED,
        command: () => {
          this.handleUserDeletion();
        },
      },
    ];
  }

}

function buildUserDetailsViewModel(
  user: User | null,
  allRoles: SelectItem[],
  userRoles: SelectItem[],
  isAdmin: boolean,
  isCurrentUser: boolean,
  loading: boolean,
) {
  const availableRoles = isAdmin ? allRoles : allRoles.filter((r) => r.value != UserRolesEnum.ROLE_SYSTEM_ADMIN);
  const isEditAllowed = isAdmin || isCurrentUser;
  return { user, loading, availableRoles, userRoles, isEditAllowed };
}

interface AccountAction {
  label: string;
  icon: string;
  disabled: boolean;
  command: () => void;
}
