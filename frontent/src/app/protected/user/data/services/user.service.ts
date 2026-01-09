import {inject, Injectable, signal} from '@angular/core';
import {GenericService} from '@core/services/generic.service';
import {UserStore} from '../store/user.store';
import {SearchService} from '@core/services/search.service';
import {TranslateService} from '@ngx-translate/core';
import {UtilService} from '@core/services/util.service';
import {
  ChangePasswordFormModel,
  changePasswordFormSchema,
  CreateUserFormModel,
  createUserFormSchema,
  UpdateUserFormModel,
  updateUserFormSchema,
  UserSearchFormModel
} from '../../forms';
import {RolesConstants} from '@core/guards/SecurityConstants';
import {SearchTableColumn} from '@models/search.model';
import {UserAccountStatusEnum} from '@models/user.model';
import {SelectItem} from 'primeng/api';
import {FieldTree, form} from '@angular/forms/signals';

@Injectable({
  providedIn: 'root'
})
export class UserService extends GenericService{

  private isUpdateFormDisabled = signal(true);
  private isChangePasswordFormDisabled = signal(true);

  public userUpdateForm: FieldTree<UpdateUserFormModel, string | number>;
  public changePasswordForm:FieldTree<ChangePasswordFormModel, string | number>;


  constructor() {
    super();
    this.userUpdateForm = form(this.updateUserModel, updateUserFormSchema(this.isUpdateFormDisabled));
    this.changePasswordForm = form(this.changePasswordModel, changePasswordFormSchema(this.isChangePasswordFormDisabled));
  }

  private userStore = inject(UserStore);
  private searchService = inject(SearchService);
  private translateService = inject(TranslateService);
  private utilService = inject(UtilService);

  public user = this.userStore.selectedUser;
  public userId = this.userStore.getUserId;
  public isLoading = this.userStore.loading;
  public criteriaCollapsed = this.userStore.criteriaCollapsed;
  public hasSearched = this.userStore.hasSearched;
  public tableLoading = this.userStore.tableLoading;
  public searchResults = this.userStore.searchResults;
  public totalCount = this.userStore.totalCount;
  public createdUserId = this.userStore.createdUserId;
  public rolesAsSelectItems = this.userStore.getUserRolesAsSelectItems;

  /**
   * Get the details of a specific user
   * @param id the id of the user
   * @returns nothing
   */
  public executeGetUserById(id:string):void{
    this.userStore.getUserById(id);
  }

  /**
   * Register a new user
   * @param form the request for creating a new user
   * @returns nothing
   */
  public executeRegisterUser(form: FieldTree<CreateUserFormModel, string | number>):void{
    const request = this.searchService.toCreateUserRequest(form);
    this.userStore.registerUser(request);
  }

  /**
   * Update a selected user
   * @param form the request for updating user
   * @returns nothing
   */
  public executeUpdateUser(form: FieldTree<UpdateUserFormModel, string | number>):void{
    const id = this.userId();
    if(id){
      const request = this.searchService.toUpdateUserRequest(form);
      this.userStore.updateUser({id,request});
    }
  }

  /**
   * Update selected user's password
   * @param form the request for updating user
   * @returns nothing
   */
  public executeChangeUserPassword(form: FieldTree<ChangePasswordFormModel, string | number>):void{
    const id = this.userId();
    if(id){
      const request = this.searchService.toChangePasswordRequest(form);
      this.userStore.changeUserPassword({id,request});
    }
  }

  /**
   * Delete a  user
   * @returns nothing
   */
  public executeDeleteUser():void{
    const id = this.userId();
    if(id){
      this.userStore.deleteUser(id);
    }
  }

  /**
   * Activate a  user
   * @returns nothing
   */
  public executeActivateUser():void{
    const id = this.userId();
    if(id){
      this.userStore.activateUser(id);
    }
  }

  /**
   * Deactivate a  user
   * @returns nothing
   */
  public executeDeactivateUser():void{
    const id = this.userId();
    if(id){
      this.userStore.deactivateUser(id);
    }
  }

  /**
   * Search for users
   * @param searchForm The search criteria
   * @returns nothing
   */
  public executeSearchUsers(searchForm: FieldTree<UserSearchFormModel, string | number>):void{
    const request = this.searchService.toUserSearchRequest(searchForm);
    this.userStore.searchUsers(request);
  }

  /**
   * Export users to csv
   * @param searchForm The search criteria
   * @returns nothing
   */
  public exportUsersToCsv(searchForm: FieldTree<UserSearchFormModel, string | number>):void{
    const request = this.searchService.toUserSearchRequest(searchForm);
    this.userStore.exportUsersToCsv(request);
  }

  /**
   * Reset search results
   * @returns nothing
   */
  public resetSearchResults():void{
    this.userStore.resetSearchResults();
  }

  /**
   * Reset created user id
   * @returns nothing
   */
  public resetCreatedUserId():void{
    this.userStore.setCreatedUserId(null);
  }

  private updateUserModel = signal<UpdateUserFormModel>({
    email: '',
    username: '',
    firstName: '',
    lastName: '',
    role: RolesConstants.ROLE_NO_ROLE,
  });



  public setUpdateFormDisabled(isDisabled: boolean): void {
    this.isUpdateFormDisabled.set(isDisabled);
  }

  public updateUserDetailsForm(data: Partial<UpdateUserFormModel>): void {
    this.updateUserModel.update(current => ({ ...current, ...data }));
  }

  private changePasswordModel = signal<ChangePasswordFormModel>({
    password:'',
    confirmPassword:''
  });

  public setChangePasswordFormDisabled(isDisabled: boolean): void {
    this.isChangePasswordFormDisabled.set(isDisabled);
  }

  public updateChangePasswordForm(data: Partial<ChangePasswordFormModel>): void {
    this.changePasswordModel.update(current => ({ ...current, ...data }));
  }


  private searchUserModel = signal<UserSearchFormModel>({
    email:'',
    username:'',
    name:'',
    role:null,
    status:UserAccountStatusEnum.ACTIVE,
    rows: 10,
    first:0,
    sortField:'',
    sortOrder: "ASC"
  });

  /**
   * Initialize the reactive form for searching users
   * @returns A FormGroup with the appropriate fields
   */
  public initSearchUserForm():FieldTree<UserSearchFormModel, string | number>{
    return form<UserSearchFormModel>(this.searchUserModel);
  }

  private createUserModel =signal<CreateUserFormModel>({
    email:'',
    username:'',
    password:'',
    confirmPassword:'',
    firstName:'',
    lastName:'',
    role:RolesConstants.ROLE_NO_ROLE
  });

  public initCreateUserForm(): FieldTree<CreateUserFormModel, string | number>{
    return form<CreateUserFormModel>(this.createUserModel,createUserFormSchema)
  }

  public markCreateUserFormAsDirty(form:FieldTree<CreateUserFormModel, string | number>):void{
    this.utilService.markAllAsDirty(form,this.createUserModel())
  }

  public markUpdateUserFormAsDirty(form:FieldTree<UpdateUserFormModel, string | number>):void{
    this.utilService.markAllAsDirty(form,this.updateUserModel())
  }

  public markUpdateUserFormAsPristine(form:FieldTree<UpdateUserFormModel, string | number>):void{
    this.utilService.markAllAsPristine(form,this.updateUserModel())
  }

  public markChangePasswordFormAsDirty(form:FieldTree<ChangePasswordFormModel, string | number>):void{
    this.utilService.markAllAsDirty(form,this.changePasswordModel())
  }

  public markChangePasswordFormAsPristine(form:FieldTree<ChangePasswordFormModel, string | number>):void{
    this.utilService.markAllAsPristine(form,this.changePasswordModel())
  }

  /**
   * Get the columns for users in order to initialize the data table
   * @returns The columns of the table
   */
  public getSearchUserTableColumns(): SearchTableColumn[] {
    const translationPrefix: string = 'USER.SEARCH.RESULTS-TABLE.COLS';
    return [
      {
        field: 'username',
        title: this.translateService.instant(`${translationPrefix}.username`),
        isLink: true,
        routerLinkConfig: {
          preRoutes: ['/', 'users'],
          postRoutes: ['details'],
        },
        dataFieldForRoute: 'publicId',
      },
      {
        field: 'firstName',
        title: this.translateService.instant(`${translationPrefix}.firstName`),
        isLink: false,
        enableSorting: true,
      },
      {
        field: 'lastName',
        title: this.translateService.instant(`${translationPrefix}.lastName`),
        enableSorting: true,
      },
      {
        field: 'email',
        title: this.translateService.instant(`${translationPrefix}.email`),
        isLink: false,
        enableSorting: true,
      },
      {
        field: 'statusLabel',
        title: this.translateService.instant(`${translationPrefix}.status`),
        isLink: false,
        enableSorting: true,
      },
    ];
  }

  /**
   * Get the user statuses
   * @returns The statuses as SelectItem array
   */
  public initUserStatuses():SelectItem[]{
    const translationPrefix: string = 'USER.STATUSES';
    return[
      {
        label:this.translateService.instant(`${translationPrefix}.active`),
        value:'account.active'
      },
      {
        label:this.translateService.instant(`${translationPrefix}.inactive`),
        value:'account.inactive'
      },
      {
        label:this.translateService.instant(`${translationPrefix}.deleted`),
        value:'account.deleted'
      }
    ];
  }
}
