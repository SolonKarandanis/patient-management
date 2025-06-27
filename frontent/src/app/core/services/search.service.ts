import {Injectable} from '@angular/core';
import {GenericService} from './generic.service';
import {ChangePasswordRequest, CreateUserRequest, UpdateUserRequest} from '@models/user.model';
import {FormGroup} from '@angular/forms';
import {ChangePasswordForm, CreateUserForm, UpdateUserForm, UserSearchForm} from '../../protected/user/forms';
import {UserSearchRequest} from '@models/search.model';

@Injectable({
  providedIn: 'root'
})
export class SearchService extends GenericService{

  /**
   * Convert from FormGroup<UpdateUserForm> to UpdateUserRequest
   * @param  form form of type UpdateUserForm
   * @returns A UpdateUserRequest
   */
  public toUpdateUserRequest(form: FormGroup<UpdateUserForm>):UpdateUserRequest{
    const {email,firstName,username,lastName,role} = form.value;
    return {
      email: email!,
      firstName: firstName!,
      username: username!,
      lastName: lastName!,
      role: role!
    };
  }

  /**
   * Convert from FormGroup<ChangePasswordForm> to ChangePasswordRequest
   * @param  form form of type ChangePasswordForm
   * @returns A ChangePasswordRequest
   */
  public toChangePasswordRequest(form:FormGroup<ChangePasswordForm>):ChangePasswordRequest{
    const {password,confirmPassword} = form.value;
    return {
      password: password!,
      confirmPassword: confirmPassword!
    };
  }

  /**
   * Convert from FormGroup<UserSearchForm> to UserSearchRequest
   * @param  searchForm form of type UserSearchForm
   * @returns A UserSearchRequest
   */
  public toUserSearchRequest(searchForm: FormGroup<UserSearchForm>):UserSearchRequest{
    const {email,name,status,role,username,rows,first,sortField,sortOrder} = searchForm.value;
    return {
      email,
      name,
      status: status!,
      roleName:role!,
      username,
      paging: {
        limit: rows!,
        page: first!,
        sortField: sortField!,
        sortDirection: sortOrder!,
      }
    };
  }

  /**
   * Convert from FormGroup<UserSearchForm> to CreateUserForm
   * @param  form form of type UserSearchForm
   * @returns A UserSearchRequest
   */
  public toCreateUserRequest(form: FormGroup<CreateUserForm>):CreateUserRequest{
    const {email,firstName,username,lastName,password,role} = form.value;
    return {
      email: email!,
      firstName: firstName!,
      lastName: lastName!,
      password: password!,
      confirmPassword:'',
      role: role!,
      username: username!
    };
  }
}
