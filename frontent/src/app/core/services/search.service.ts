import {Injectable} from '@angular/core';
import {GenericService} from './generic.service';
import {ChangePasswordRequest, CreateUserRequest, UpdateUserRequest} from '@models/user.model';
import {FormGroup} from '@angular/forms';
import {
  ChangePasswordForm, ChangePasswordFormModel,
  CreateUserFormModel,
  UpdateUserForm, UpdateUserFormModel,
  UserSearchFormModel
} from '../../protected/user/forms';
import {I18nResourceSearchRequest, UserSearchRequest} from '@models/search.model';
import {I18nResourceSearchForm, I18nResourceSearchFormModel} from '../../protected/i18n/forms';
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
  public toUpdateUserRequest(form: FieldTree<UpdateUserFormModel, string | number>):UpdateUserRequest{
    return {
      email: form.email().value(),
      firstName: form.firstName().value(),
      username: form.username().value(),
      lastName: form.lastName().value(),
      role: form.role().value()
    };
  }

  /**
   * Convert from FormGroup<ChangePasswordForm> to ChangePasswordRequest
   * @param  form form of type ChangePasswordForm
   * @returns A ChangePasswordRequest
   */
  public toChangePasswordRequest(form:FieldTree<ChangePasswordFormModel, string | number>):ChangePasswordRequest{
    return {
      password:form.password().value(),
      confirmPassword: form.confirmPassword().value()
    };
  }

  /**
   * Convert from FormGroup<UserSearchForm> to UserSearchRequest
   * @param  form form of type UserSearchForm
   * @returns A UserSearchRequest
   */
  public toUserSearchRequest(form: FieldTree<UserSearchFormModel, string | number>):UserSearchRequest{
    return {
      email:form.email().value(),
      name:form.name().value(),
      status: form.status().value(),
      roleName:form.role().value()!,
      username:form.username().value(),
      paging: {
        limit: form.rows().value(),
        page: form.first().value(),
        sortField: form.sortField().value(),
        sortDirection:form.sortOrder().value(),
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

  public toI18nResourceSearchRequest(form:  FieldTree<I18nResourceSearchFormModel, string | number>):I18nResourceSearchRequest{
    const {language,term, module, rows, first, sortField, sortOrder} = form;
    return {
      languageId:language().value()!,
      term:term().value(),
      moduleId:module().value()!,
      paging: {
        limit: rows().value(),
        page: first().value(),
        sortField: sortField().value(),
        sortDirection:sortOrder().value(),
      }
    }
  }
}
