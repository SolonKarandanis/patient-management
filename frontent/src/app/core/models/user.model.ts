import {RolesConstants} from '../guards/SecurityConstants';

export enum UserAccountStatus{
  ACTIVE ="account.active",
  INACTIVE="account.inactive",
  DELETED="account.deleted"
}

export interface BaseUserModel{
  username: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface UserWithRole extends BaseUserModel{
  role:RolesConstants;
}

export interface UserModel extends UserWithRole{
  publicId: string;
  status: UserAccountStatus;
  statusLabel:string;
  createdDate:string;
  lastModifiedDate:string;
  isEnabled:boolean;
  isVerified:boolean;
  authorities:string[]
}

export interface CreateUserRequest extends UserWithRole{
  password:string;
}

export interface UpdateUserRequest extends UserWithRole{
}

