import { FormControl } from "@angular/forms";
import {UserAccountStatus} from '@models/user.model';
import {RolesConstants} from '@core/guards/SecurityConstants';


export interface UserSearchForm{
  email: FormControl<string|null|undefined>;
  username: FormControl<string|null|undefined>;
  name: FormControl<string|null|undefined>;
  status: FormControl<UserAccountStatus>;
  role:FormControl<RolesConstants|null>
  rows:FormControl<number>;
  first:FormControl<number>;
}

export interface UpdateUserForm{
  username: FormControl<string|null|undefined>;
  firstName: FormControl<string|null|undefined>;
  lastName: FormControl<string|null|undefined>;
  email: FormControl<string|null|undefined>;
  role:FormControl<RolesConstants>;
}

export interface CreateUserForm{
  username: FormControl<string|null>;
  password: FormControl<string|null>;
  confirmPassword: FormControl<string|null>;
  firstName: FormControl<string|null>;
  lastName: FormControl<string|null>;
  email: FormControl<string|null>;
  role:FormControl<RolesConstants|null>;
}
