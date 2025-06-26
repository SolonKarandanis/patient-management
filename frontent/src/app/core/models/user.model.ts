import {BaseModel} from '@models/base.model';

export const UserAccountStatusEnum = {
  ACTIVE: "account.active",
  INACTIVE: "account.inactive",
  DELETED: "account.deleted",
} as const satisfies Record<string, string>;

export type UserAccountStatus = (typeof UserAccountStatusEnum)[keyof typeof UserAccountStatusEnum];

export interface BaseUserModel extends BaseModel{
  username: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface Role{
  id:number;
  name:string;
  nameLabel?:string;
}

export interface Operation{
  id:number;
  name:string;
}

export interface UserWithRole extends BaseUserModel{
  roles:Role[];
}

export interface User extends UserWithRole{
  status: UserAccountStatus;
  statusLabel:string;
  createdDate:string;
  lastModifiedDate:string;
  isEnabled:boolean;
  isVerified:boolean;
  operations:Operation[];
}

export interface ChangePasswordRequest{
  password:string;
  confirmPassword:string;
}

export interface CreateUserRequest extends ChangePasswordRequest{
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  role:string;
}

export interface UpdateUserRequest{
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  role:string;
}

