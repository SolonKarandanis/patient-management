import {User} from '@models/user.model';

export type UserSearchState = {
  readonly searchResults: User[];
  readonly totalCount: number;
}

export const initialUserSearchState: UserSearchState = {
  searchResults: [],
  totalCount: 0,
};
