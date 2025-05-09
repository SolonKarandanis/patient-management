import {Operation, Role, User} from '@models/user.model';

export interface AuthState{
  readonly isLoggedIn: boolean,
  readonly authToken: string| undefined;
  readonly expires: string|undefined;
  readonly user: User | undefined;
  readonly roles:Role[] | undefined;
  readonly operations: Operation[] | undefined;
}

export const initialAuthState: AuthState = {
  isLoggedIn: false,
  authToken: undefined,
  expires:undefined,
  user: undefined,
  roles:undefined,
  operations:undefined,
};
