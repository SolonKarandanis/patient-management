import {Operation, Role, User} from '../../models/user.model';

export type AuthState ={
  readonly isLoggedIn: boolean,
  readonly loading: boolean,
  readonly errorMessage: string| null;
  readonly showError: boolean;
  readonly authToken: string| undefined;
  readonly expires: string|undefined;
  readonly user: User | undefined;
  readonly roles:Role[] | undefined;
  readonly operations: Operation[] | undefined;
}

export const initialAuthState: AuthState = {
  isLoggedIn: false,
  loading: false,
  errorMessage:null,
  showError:false,
  authToken: undefined,
  expires:undefined,
  user: undefined,
  roles:undefined,
  operations:undefined
};
