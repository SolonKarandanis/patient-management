import {ChangeDetectionStrategy, Component, computed, inject} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe} from '@ngx-translate/core';
import {UserService} from '../data/services/user.service';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {BaseComponent} from '@shared/abstract/BaseComponent';
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
            [allowSave]="form.valid"
            (saveClicked)="detailsSaveClickHandler()"
            (validateFormClicked)="detailsSaveFormValidateHandler()"
            (resetFormValidityClicked)="detailsSaveFormResetValidationHandler()"
            (editModeChanged)="detailsEditHandler($event)">
            <app-user-details-form
              [formGroup]="form"
              [fetchingData]="vm.loading"
              [availableRoles]="vm.availableRoles"/>
          </app-fieldset>
          <app-fieldset
            legend="{{ 'USER.DETAILS.LABELS.change-password' | translate }}"
            [toggleable]="false"
            [allowEdit]="vm.isEditAllowed"
            [allowSave]="changePasswordForm.valid"
            (saveClicked)="changePasswordSaveClickHandler()"
            (validateFormClicked)="changePasswordFormValidateHandler()"
            (resetFormValidityClicked)="changePasswordFormResetValidationHandler()"
            (editModeChanged)="changePasswordEditHandler($event)">
            <app-user-password-change-form [formGroup]="changePasswordForm" />
          </app-fieldset>
          <app-fieldset
            legend="{{ 'USER.DETAILS.LABELS.account-status' | translate }}"
            [toggleable]="false"
            [allowEdit]="false">
            <div class="flex justify-between">
              <p class="py-2 text-xl font-semibold">Manage Account</p>
              <p-splitbutton
                label="Account Actions"
                dropdownIcon="pi pi-cog"
                [disabled]="!vm.isEditAllowed"
                [model]="accountActions" />
            </div>
          </app-fieldset>
        }
      }
    </div>
  `,
  styleUrl: './user-details.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent extends BaseComponent {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private utilService = inject(UtilService);
  private commonEntitiesService = inject(CommonEntitiesService);

  protected changePasswordForm!: FormGroup;
  protected accountActions!:MenuItem[];

  constructor() {
    super();
    const userId = injectParams('id')();
    this.userService.executeGetUserById(userId as string);
  }

  protected vm = computed(()=>{
    const loading = this.userService.isLoading();
    const user = this. userService.user();
    const userRoles=this.userService.rolesAsSelectItems();
    let availableRoles = this.commonEntitiesService.rolesAsSelectItems();

    const isAdmin =this.authService.hasRole(UserRolesEnum.ROLE_SYSTEM_ADMIN)();
    if(!isAdmin){
      availableRoles=availableRoles.filter(r=>r.value!=UserRolesEnum.ROLE_SYSTEM_ADMIN);
    }
    const isEditAllowed =  isAdmin || this.authService.isUserMe(user?.publicId)();

    if(user){
      this.initDetailsForm();
      this.initChangePasswordForm();
      this.initMenuActions(user.status);
      this.form.patchValue({
        username:user.username,
        firstName:user.firstName,
        lastName:user.lastName,
        email:user.email,
        role:userRoles[0].value,
      });
    }

    return {
      user,
      loading,
      availableRoles,
      userRoles,
      isEditAllowed
    }
  });


  protected detailsSaveFormValidateHandler():void{
    if(!this.form.valid){
      this.utilService.markAllAsDirty(this.form);
    }
  }

  protected detailsSaveFormResetValidationHandler():void{
    this.utilService.markAllAsPristine(this.changePasswordForm)
  }

  protected detailsSaveClickHandler():void{
    if(this.form.valid){
      this.userService.executeUpdateUser(this.form);
    }
  }

  protected detailsEditHandler(isEditMode: boolean):void{
    isEditMode ?  this.form.enable():this.form.disable();
  }



  protected changePasswordFormValidateHandler():void{
    if(!this.changePasswordForm.valid){
      this.utilService.markAllAsDirty(this.changePasswordForm);
    }
  }

  protected changePasswordFormResetValidationHandler():void{
    this.utilService.markAllAsPristine(this.changePasswordForm);
  }

  protected changePasswordSaveClickHandler():void{
    if(this.changePasswordForm.valid){
      this.userService.executeChangeUserPassword(this.changePasswordForm);
    }
  }

  protected changePasswordEditHandler(isEditMode: boolean):void{
    isEditMode ?  this.changePasswordForm.enable():this.changePasswordForm.disable();
  }

  private initDetailsForm():void{
    this.form = this.userService.initUpdateUserForm();
    this.form.disable();
  }

  private initChangePasswordForm():void{
    this.changePasswordForm = this.userService.initChangePasswordForm();
    this.changePasswordForm.disable();
  }

  private initMenuActions(status:string):void{
    console.log(status)
    this.accountActions=[
      {
        label: 'Activate',
        icon: 'pi pi-check',
        disabled: status ===UserAccountStatusEnum.ACTIVE,
        command: () => {

        },
      },
      {
        label: 'Deactivate',
        icon: 'pi pi-ban',
        disabled: status ===UserAccountStatusEnum.INACTIVE,
        command: () => {

        },
      },
      {
        label: 'Delete',
        icon: 'pi pi-times',
        disabled: status ===UserAccountStatusEnum.DELETED,
        command: () => {

        },
      },
    ];
  }

}
