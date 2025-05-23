import {Operation, Role, User} from '@models/user.model';

export type UserState ={
  readonly loading: boolean,
  readonly errorMessage: string| null;
  readonly showError: boolean;
  readonly selectedUser: User| null;
  readonly searchResults: User[];
  readonly totalCount:number| null;
  readonly createdUserId:string|null;
  readonly roles:Role[];
  readonly operations: Operation[];
}

export const initialUserState: UserState = {
  loading: false,
  errorMessage:null,
  showError:false,
  selectedUser: null,
  searchResults: [],
  totalCount: null,
  createdUserId:null,
  roles: [],
  operations: [],
};
