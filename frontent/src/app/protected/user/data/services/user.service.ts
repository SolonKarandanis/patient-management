import {inject, Injectable} from '@angular/core';
import {GenericService} from '@core/services/generic.service';
import {UserStore} from '../store/user.store';
import {SearchService} from '@core/services/search.service';
import {TranslateService} from '@ngx-translate/core';
import {UtilService} from '@core/services/util.service';
import {CreateUserForm, UpdateUserForm, UserSearchForm} from '../../forms';
import {AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {RolesConstants} from '@core/guards/SecurityConstants';
import {SearchTableColumn} from '@models/search.model';
import {User, UserAccountStatusEnum} from '@models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService extends GenericService{
  private userStore = inject(UserStore);
  private searchService = inject(SearchService);
  private translateService = inject(TranslateService);
  private utilService = inject(UtilService);

  public user = this.userStore.getUser;
  public userId = this.userStore.getUserId;
  public isLoading = this.userStore.loading;
  public criteriaCollapsed = this.userStore.criteriaCollapsed;
  public hasSearched = this.userStore.hasSearched;
  public tableLoading = this.userStore.tableLoading;
  public searchResults = this.userStore.searchResults;
  public totalCount = this.userStore.totalCount;
  public createdUserId = this.userStore.createdUserId;

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
  public executeRegisterUser(form: FormGroup<CreateUserForm>):void{
    const request = this.searchService.toCreateUserRequest(form);
    this.userStore.registerUser(request);
  }

  /**
   * Update a selected user
   * @param form the request for updating user
   * @returns nothing
   */
  public executeUpdateUser(form: FormGroup<UpdateUserForm>):void{
    const id = this.userId();
    if(id){
      const request = this.searchService.toUpdateUserRequest(form);
      this.userStore.updateUser({id,request});
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
  public executeSearchUsers(searchForm: FormGroup<UserSearchForm>):void{
    const request = this.searchService.toUserSearchRequest(searchForm);
    this.userStore.searchUsers(request);
  }

  /**
   * Export users to csv
   * @param searchForm The search criteria
   * @returns nothing
   */
  public exportUsersToCsv(searchForm: FormGroup<UserSearchForm>):void{
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

  /**
   * Initialize the reactive form for updating a user
   * @returns A FormGroup with the appropriate fields
   */
  public initUpdateUserForm(user:User | null | undefined): FormGroup<UpdateUserForm>{
    return this.formBuilder.group<UpdateUserForm>({
      username: new FormControl(user?.username),
      firstName: new FormControl(user?.firstName),
      lastName: new FormControl(user?.lastName),
      email: new FormControl(user?.email),
      role: new FormControl(RolesConstants.ROLE_NO_ROLE,{nonNullable: true}),
    })
  }

  /**
   * Initialize the reactive form for searching users
   * @returns A FormGroup with the appropriate fields
   */
  public initSearchUserForm(): FormGroup<UserSearchForm>{
    return this.formBuilder.group<UserSearchForm>({
      email: new FormControl(null),
      name: new FormControl(null),
      status: new FormControl(UserAccountStatusEnum.ACTIVE,{nonNullable: true}),
      username: new FormControl(null),
      role: new FormControl(null),
      rows:new FormControl(10,{nonNullable: true}),
      first: new FormControl(0,{nonNullable: true}),
      sortField: new FormControl('',{nonNullable: true}),
      sortOrder: new FormControl('ASC',{nonNullable: true})
    })
  }

  /**
   * Initialize the reactive form for creating users
   * @returns A FormGroup with the appropriate fields
   */
  public initCreateUserForm():FormGroup<CreateUserForm>{
    return this.formBuilder.group<CreateUserForm>({
      email: new FormControl(null,[Validators.required,]),
      username: new FormControl(null,[Validators.required,]),
      password:new FormControl(null,[Validators.required,]),
      confirmPassword:new FormControl(null,[Validators.required,]),
      firstName: new FormControl(null,[Validators.required,]),
      lastName:new FormControl(null,[Validators.required,]),
      role:new FormControl(RolesConstants.ROLE_NO_ROLE,[Validators.required])
    },{validators: this.samePasswords()});
  }

  public samePasswords(): ValidatorFn {
    return (frmGroup: AbstractControl): ValidationErrors | null => {
      const pass: string = frmGroup.get('password')?.value;
      const passConf: string = frmGroup.get('confirmPassword')?.value;
      const samePass: boolean = pass === passConf;

      return samePass ? null : { samePassword: {message: "Passwords don't match"} };
    };
  }

  public strongPassword(isViewEdit?: boolean): ValidatorFn {
    return (frmGroup: AbstractControl): ValidationErrors | null => {
      if (isViewEdit && frmGroup.value && frmGroup.value.length === 0) {
        return null;
      }

      const regExp: RegExp = new RegExp(this.utilService.strongPasswordRegex);
      const res = regExp.test(frmGroup.value);
      return res ? null : { strongPassword: true };
    };
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
}
