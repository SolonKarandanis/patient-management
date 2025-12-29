import {Injectable} from '@angular/core';
import {GenericService} from './generic.service';
import {ChangePasswordRequest, CreateUserRequest, UpdateUserRequest} from '@models/user.model';
import {FormGroup} from '@angular/forms';
import {
  ChangePasswordForm,
  CreateUserForm,
  CreateUserFormModel,
  UpdateUserForm,
  UserSearchForm
} from '../../protected/user/forms';
import {I18nResourceSearchRequest, UserSearchRequest} from '@models/search.model';
import {I18nResourceSearchForm} from '../../protected/i18n/forms';
import {FieldTree} from '@angular/forms/signals';

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
   * Convert from FieldTree<CreateUserFormModel, string | number>
   * @param  form form of type UserSearchForm
   * @returns A CreateUserRequest
   */
  public toCreateUserRequest(form: FieldTree<CreateUserFormModel, string | number>):CreateUserRequest{
    return {
      email: form.email().value(),
      firstName: form.firstName().value(),
      lastName: form.lastName().value(),
      password: form.password().value(),
      confirmPassword:form.confirmPassword().value(),
      role: form.role().value()!,
      username: form.username().value()
    };
  }

  public toI18nResourceSearchRequest(form: FormGroup<I18nResourceSearchForm>):I18nResourceSearchRequest{
    const {language,module,term,rows,first,sortField,sortOrder} = form.value
    return {
      languageId:language!,
      term:term!,
      moduleId:module!,
      paging: {
        limit: rows!,
        page: first!,
        sortField: sortField!,
        sortDirection: sortOrder!,
      }
    }
  }
}
