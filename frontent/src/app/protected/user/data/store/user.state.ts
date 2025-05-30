import {Operation, Role, User} from '@models/user.model';

export type UserState ={
  readonly selectedUser: User| null;
  readonly searchResults: User[];
  readonly totalCount:number;
  readonly createdUserId:string|null;
  readonly roles:Role[];
  readonly operations: Operation[];
}

export const initialUserState: UserState = {
  selectedUser: null,
  searchResults: [],
  totalCount: 0,
  createdUserId:null,
  roles: [],
  operations: [],
};
