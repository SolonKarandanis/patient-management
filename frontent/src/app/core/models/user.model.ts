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

export interface Role{
  id:number;
  name:string;
}

export interface Operation{
  id:number;
  name:string;
}

export interface UserWithRole extends BaseUserModel{
  roles:Role[];
}

export interface User extends UserWithRole{
  publicId: string;
  status: UserAccountStatus;
  statusLabel:string;
  createdDate:string;
  lastModifiedDate:string;
  isEnabled:boolean;
  isVerified:boolean;
  operations:Operation[];
}

export interface CreateUserRequest{
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  password:string;
  role:string;
}

export interface UpdateUserRequest{
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  role:string;
}

