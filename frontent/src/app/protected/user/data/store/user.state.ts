import {Operation, Role, User} from '@models/user.model';

export type UserState ={
  readonly selectedUser: User| null;
  readonly searchResults: User[];
  readonly totalCount:number| null;
  readonly createdUserId:string|null;
  readonly roles:Role[];
  readonly operations: Operation[];
}

export const initialUserState: UserState = {
  selectedUser: null,
  searchResults: [],
  totalCount: null,
  createdUserId:null,
  roles: [],
  operations: [],
};
