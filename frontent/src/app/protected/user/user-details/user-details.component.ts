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
            (saveClicked)="detailsSaveClickHandler()"
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
            (saveClicked)="changePasswordSaveClickHandler()"
            (editModeChanged)="changePasswordEditHandler($event)">

          </app-fieldset>
        }
      }
    </div>
  `,
  styleUrl: './user-details.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent extends BaseComponent{
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private commonEntitiesService = inject(CommonEntitiesService);

  protected changePasswordForm!: FormGroup;

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
      availableRoles=availableRoles.filter(r=>r.value!=UserRolesEnum.ROLE_SYSTEM_ADMIN)
    }
    const isEditAllowed =  isAdmin || this.authService.isUserMe(user?.publicId)();

    if(user){
      this.initDetailsForm();
      this.initChangePasswordForm();
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

  protected detailsSaveClickHandler():void{
    this.userService.executeUpdateUser(this.form);
  }

  protected detailsEditHandler(isEditMode: boolean):void{
    isEditMode ?  this.form.enable():this.form.disable();
  }

  protected changePasswordSaveClickHandler():void{
    this.userService.executeChangeUserPassword(this.changePasswordForm);
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

}
