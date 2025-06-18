import {ChangeDetectionStrategy, Component, computed, inject, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe} from '@ngx-translate/core';
import {UserService} from '../data/services/user.service';
import {USER_DETAILS_PROVIDER,  USERS_DETAILS} from './user-details.provider';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UserDetailsFormComponent} from '../user-details-form/user-details-form.component';
import {FieldsetComponent} from '@components/fieldset/fieldset.component';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {SelectItem} from 'primeng/api';
import {User} from '@models/user.model';


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
          <app-fieldset legend="Details" [toggleable]="false" >
            <app-user-details-form
              [formGroup]="form"
              [fetchingData]="vm.loading"
              [availableRoles]="vm.availableRoles"/>
          </app-fieldset>
        }
      }
    </div>
  `,
  styleUrl: './user-details.component.css',
  providers:[
    USER_DETAILS_PROVIDER
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent extends BaseComponent implements OnInit{
  private userService = inject(UserService);
  protected commonEntitiesService = inject(CommonEntitiesService);
  protected user = inject(USERS_DETAILS);
  protected loading = this.userService.isLoading;
  protected userRoles=this.userService.rolesAsSelectItems;

  protected vm = computed(()=>{
    const loading = this.loading();
    const user = this.user();
    const userRoles=this.userRoles();
    const availableRoles = this.commonEntitiesService.rolesAsSelectItems();

    if(user){
      this.initForm(user);
      this.form.patchValue({
        role:userRoles[0].value,
      });
    }

    return {
      user,
      loading,
      availableRoles,
      userRoles
    }
  });

  ngOnInit(): void {

  }

  private initForm(user:User):void{
    this.form = this.userService.initUpdateUserForm(user);
  }

}
