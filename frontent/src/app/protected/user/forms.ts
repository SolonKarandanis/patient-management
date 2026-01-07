import { FormControl } from "@angular/forms";
import {UserAccountStatus} from '@models/user.model';
import {RolesConstants} from '@core/guards/SecurityConstants';
import {SortDirection} from '@models/search.model';
import {customError, email, required, schema, validate} from '@angular/forms/signals';


export interface UserSearchForm{
  email: FormControl<string|null|undefined>;
  username: FormControl<string|null|undefined>;
  name: FormControl<string|null|undefined>;
  status: FormControl<UserAccountStatus>;
  role:FormControl<RolesConstants|null>
  rows:FormControl<number>;
  first:FormControl<number>;
  sortField:FormControl<string>;
  sortOrder:FormControl<SortDirection>;
}

export interface UserSearchFormModel{
  email: string;
  username:string;
  name:string;
  status: UserAccountStatus;
  role: RolesConstants|null;
  rows:number;
  first:number;
  sortField:string;
  sortOrder:SortDirection;
}


export interface UpdateUserFormModel{
  email: string;
  username:string;
  firstName:string;
  lastName: string;
  role: RolesConstants;
}

export interface UpdateUserForm{
  username: FormControl<string|null|undefined>;
  firstName: FormControl<string|null|undefined>;
  lastName: FormControl<string|null|undefined>;
  email: FormControl<string|null|undefined>;
  role:FormControl<RolesConstants>;
}

export interface ChangePasswordFormModel{
  password: string;
  confirmPassword:string;
}

export interface ChangePasswordForm{
  password: FormControl<string|null>;
  confirmPassword: FormControl<string|null>;
}

export interface CreateUserFormModel{
  username:string;
  password:string;
  confirmPassword:string;
  firstName:string;
  lastName:string;
  email:string;
  role:RolesConstants|null;
}

export const createUserFormSchema = schema<CreateUserFormModel>((field) => {
  required(field.email,{
    message:'email-required',
  });
  email(field.email);

  required(field.username,{
    message:'username-required'
  });
  required(field.password,{
    message:'password-required'
  });
  required(field.confirmPassword,{
    message:'confirmPassword-required'
  });
  required(field.firstName,{
    message:'firstName-required'
  });
  required(field.lastName,{
    message:'lastName-required'
  });
  required(field.role,{
    message:'role-required'
  });

  validate(field.confirmPassword, (context)=>{
    const password = context.valueOf(field.password);
    const confirmPassword = context.valueOf(field.confirmPassword);

    const samePass: boolean = password === confirmPassword;
    return samePass? null : customError({message:"passwordMismatch",kind:'passwordMismatch'});
  })
})

export interface CreateUserForm{
  username: FormControl<string|null>;
  password: FormControl<string|null>;
  confirmPassword: FormControl<string|null>;
  firstName: FormControl<string|null>;
  lastName: FormControl<string|null>;
  email: FormControl<string|null>;
  role:FormControl<RolesConstants|null>;
}
