import {ChangeDetectionStrategy, Component, computed, effect, inject} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {UserService} from '../data/services/user.service';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UserDetailsFormComponent} from '../user-details-form/user-details-form.component';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {injectParams} from '@shared/utils/injectParams';
import {AuthService} from '@core/services/auth.service';
import {UserRolesEnum} from '@models/constants';
import {UserPasswordChangeFormComponent} from '../user-password-change-form/user-password-change-form.component';
import {UtilService} from '@core/services/util.service';
import {SplitButton} from 'primeng/splitbutton';
import {MenuItem} from 'primeng/api';
import {UserAccountStatusEnum} from '@models/user.model';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {FieldTree} from '@angular/forms/signals';
import {ChangePasswordFormModel, UpdateUserFormModel} from '../forms';



@Component({
  selector: 'app-user-details',
  imports: [
    PageHeaderComponent,
    TranslatePipe,
    RequiredFieldsLabelComponent,
    FormsModule,
    ReactiveFormsModule,
    UserDetailsFormComponent,
    FieldsetComponent,
    UserPasswordChangeFormComponent,
    SplitButton,
    ConfirmDialog,
  ],
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
              <p-splitbutton
                label="{{ 'USER.DETAILS.LABELS.account-actions' | translate }}"
                dropdownIcon="pi pi-cog"
                [disabled]="!vm.isEditAllowed"
                [model]="accountActions" />
            </div>
          </app-fieldset>
        }
      }
    </div>
    <p-confirmDialog
        icon="pi pi-exclamation-triangle"
        defaultFocus="none">
    </p-confirmDialog>
  `,
  styleUrl: './user-details.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent  {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private utilService = inject(UtilService);
  private commonEntitiesService = inject(CommonEntitiesService);
  private translate = inject(TranslateService);

  protected changePasswordForm!: FieldTree<ChangePasswordFormModel, string | number>;
  protected accountActions!:MenuItem[];

  protected form: FieldTree<UpdateUserFormModel, string | number>;

  constructor() {
    this.form = this.userService.userUpdateForm;
    const userId = injectParams('id')();
    this.userService.executeGetUserById(userId as string);

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

  protected vm = computed(()=>{
    const user = this.userService.user();
    let availableRoles = this.commonEntitiesService.rolesAsSelectItems();

    const isAdmin =this.authService.hasRole(UserRolesEnum.ROLE_SYSTEM_ADMIN)();
    if(!isAdmin){
      availableRoles=availableRoles.filter(r=>r.value!=UserRolesEnum.ROLE_SYSTEM_ADMIN);
    }
    const isEditAllowed =  isAdmin || this.authService.isUserMe(user?.publicId)();

    return {
      user,
      loading: this.userService.isLoading(),
      availableRoles,
      userRoles: this.userService.rolesAsSelectItems(),
      isEditAllowed,
    }
  });


  protected detailsSaveFormValidateHandler():void{
    // if(!this.form.valid){
    //   this.utilService.markAllAsDirty(this.form);
    // }
  }

  protected detailsSaveFormResetValidationHandler():void{
    // this.utilService.markAllAsPristine(this.changePasswordForm)
  }

  protected detailsSaveClickHandler():void{
    // if(!this.form.valid){
    //   this.userService.executeUpdateUser(this.form);
    // }
  }

  protected detailsEditHandler(isEditMode: boolean):void{
    this.userService.setUpdateFormDisabled(!isEditMode);
  }

  protected changePasswordFormValidateHandler():void{
    // if(!this.changePasswordForm.valid){
    //   this.utilService.markAllAsDirty(this.changePasswordForm);
    // }
  }

  protected changePasswordFormResetValidationHandler():void{
    // this.utilService.markAllAsPristine(this.changePasswordForm);
  }

  protected changePasswordSaveClickHandler():void{
    // if(this.changePasswordForm.valid){
    //   this.userService.executeChangeUserPassword(this.changePasswordForm);
    // }
  }

  protected changePasswordEditHandler(isEditMode: boolean):void{
    this.userService.setChangePasswordFormDisabled(!isEditMode);
  }

  private initChangePasswordForm():void{
    this.changePasswordForm = this.userService.changePasswordForm;
    this.userService.setChangePasswordFormDisabled(true);
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
        icon: 'pi pi-check',
        disabled: status ===UserAccountStatusEnum.ACTIVE,
        command: () => {
          this.handleUserActivation();
        },
      },
      {
        label: this.translate.instant(`${translationPrefix}.deactivate`),
        icon: 'pi pi-ban',
        disabled: status ===UserAccountStatusEnum.INACTIVE,
        command: () => {
          this.handleUserDeActivation();
        },
      },
      {
        label: this.translate.instant(`${translationPrefix}.delete`),
        icon: 'pi pi-times',
        disabled: status ===UserAccountStatusEnum.DELETED,
        command: () => {
          this.handleUserDeletion();
        },
      },
    ];
  }

}
