import {User} from '@models/user.model';

export type UserDetailState = {
  readonly selectedUser: User | null;
  readonly createdUserId: string | null;
}

export const initialUserDetailState: UserDetailState = {
  selectedUser: null,
  createdUserId: null,
};
