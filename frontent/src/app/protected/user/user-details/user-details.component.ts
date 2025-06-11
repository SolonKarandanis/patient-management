import {ChangeDetectionStrategy, Component, computed, inject, OnInit, signal} from '@angular/core';
import {PageHeaderComponent} from '@components/page-header/page-header.component';
import {TranslatePipe} from '@ngx-translate/core';
import {UserService} from '../data/services/user.service';
import {USER_DETAILS_PROVIDERS, USERS_DETAILS} from './user-details.provider';
import {RequiredFieldsLabelComponent} from '@components/required-fields-label/required-fields-label.component';
import {BaseComponent} from '@shared/abstract/BaseComponent';
import {FloatLabel} from 'primeng/floatlabel';
import {FormErrorComponent} from '@components/form-error/form-error.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';


@Component({
  selector: 'app-user-details',
  imports: [
    PageHeaderComponent,
    TranslatePipe,
    RequiredFieldsLabelComponent,
    FloatLabel,
    FormErrorComponent,
    FormsModule,
    InputText,
    ReactiveFormsModule,

  ],
  template: `
    <div
      class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0 text-black">
      <app-page-header>
        {{ 'USER.DETAILS.title' | translate }}
      </app-page-header>
      <app-required-fields-label/>
      @if (vm(); as vm) {
        @let user = vm.user;
        <form [formGroup]="form">
          <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
            <div class="grid gap-6 mb-6 md:grid-cols-2">
              <div class="mt-6">
                <p-float-label variant="on" class="w-full mb-3">
                  <input
                    id="username"
                    pInputText
                    type="text"
                    class="border-0 px-3 py-3 !bg-white text-sm shadow w-full !text-black"
                    formControlName="username"
                    autocomplete="username"/>
                  <label for="email">{{ 'USER.SEARCH.LABELS.email' | translate }}</label>
                </p-float-label>
                <app-form-error
                  [displayLabels]="isFieldValid('username')"
                  [validationErrors]="form.get('username')?.errors"/>
              </div>
              <div>
                <label for="first_name" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">First
                  name</label>
                <input type="text" id="first_name"
                       class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                       placeholder="John" required/>
              </div>
              <div>
                <label for="last_name" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Last
                  name</label>
                <input type="text" id="last_name"
                       class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                       placeholder="Doe" required/>
              </div>
            </div>
          </div>
        </form>
      }
    </div>
  `,
  styleUrl: './user-details.component.css',
  providers:[
    USER_DETAILS_PROVIDERS
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent extends BaseComponent implements OnInit{
  private userService = inject(UserService);
  protected user = inject(USERS_DETAILS);
  protected editMode= signal(false);
  protected loading = this.userService.isLoading;

  protected vm = computed(()=>{
    const loading = this.loading();
    const user = this.user();
    const editMode = this.editMode();

    if(user){
      this.form.patchValue({
        username: user.username,
        firstName:user.firstName,
        lastName:user.lastName,
        email:user.email
      });
    }

    return {
      user,
      loading,
      editMode
    }
  });

  ngOnInit(): void {
    this.initForm();
  }

  private initForm():void{
    this.form = this.userService.initUpdateUserForm();
  }

}
