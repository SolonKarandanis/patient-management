import {Injectable} from '@angular/core';
import {ChangePasswordRequest, CreateUserRequest, UpdateUserRequest} from '@models/user.model';
import {
  ChangePasswordFormModel,
  CreateUserFormModel,
  UpdateUserFormModel,
  UserSearchFormModel
} from '../..';
import {UserSearchRequest} from '@models/search.model';
import {FieldTree} from '@angular/forms/signals';

@Injectable({
  providedIn: 'root'
})
export class UserSearchMapperService {

  public toUpdateUserRequest(form: FieldTree<UpdateUserFormModel, string | number>):UpdateUserRequest{
    return {
      email: form.email().value(),
      firstName: form.firstName().value(),
      username: form.username().value(),
      lastName: form.lastName().value(),
      role: form.role().value()
    };
  }

  public toChangePasswordRequest(form:FieldTree<ChangePasswordFormModel, string | number>):ChangePasswordRequest{
    return {
      password:form.password().value(),
      confirmPassword: form.confirmPassword().value()
    };
  }

  public toUserSearchRequest(form: FieldTree<UserSearchFormModel, string | number>):UserSearchRequest{
    return {
      searchMethod: form.searchMethod().value(),
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
}
