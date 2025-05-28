import {Injectable} from '@angular/core';
import {GenericService} from './generic.service';
import {CreateUserRequest, UpdateUserRequest} from '@models/user.model';
import {FormGroup} from '@angular/forms';
import {CreateUserForm, UpdateUserForm, UserSearchForm} from '../../protected/user/forms';
import {UserSearchRequest} from '@models/search.model';

@Injectable({
  providedIn: 'root'
})
export class SearchService extends GenericService{

  /**
   * Convert from FormGroup<UpdateUserForm> to UpdateUserRequest
   * @param  searchForm form of type UpdateUserForm
   * @returns A UpdateUserRequest
   */
  public toUpdateUserRequest(searchForm: FormGroup<UpdateUserForm>):UpdateUserRequest{
    const {email,firstName,username,lastName,role} = searchForm.value;
    return {
      email: email!,
      firstName: firstName!,
      username: username!,
      lastName: lastName!,
      role: role!
    };
  }

  /**
   * Convert from FormGroup<UserSearchForm> to UserSearchRequest
   * @param  searchForm form of type UserSearchForm
   * @returns A UserSearchRequest
   */
  public toUserSearchRequest(searchForm: FormGroup<UserSearchForm>):UserSearchRequest{
    const {email,name,status,role,username,rows,first} = searchForm.value;
    return {
      email,
      name,
      status: status!,
      roleName:role!,
      username,
      paging: {
        limit: rows!,
        page: first!
      }
    };
  }

  /**
   * Convert from FormGroup<UserSearchForm> to CreateUserForm
   * @param  searchForm form of type UserSearchForm
   * @returns A UserSearchRequest
   */
  public toCreateUserRequest(searchForm: FormGroup<CreateUserForm>):CreateUserRequest{
    const {email,firstName,username,lastName,password,role} = searchForm.value;
    return {
      email: email!,
      firstName: firstName!,
      lastName: lastName!,
      password: password!,
      role: role!,
      username: username!
    };
  }
}
