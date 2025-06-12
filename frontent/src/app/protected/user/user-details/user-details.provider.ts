import {InjectionToken, Provider, Signal} from '@angular/core';
import {UserService} from '../data/services/user.service';
import {User} from '@models/user.model';
import {injectParams} from '@shared/utils/injectParams';

export interface UserDetailsVM{
  user:Signal<User | null>
}

export const USERS_DETAILS = new InjectionToken<Signal<User | null>>(
  'A stream with selected user information',
);


export const USER_DETAILS_PROVIDER: Provider[] =[
  {
    provide: USERS_DETAILS,
    deps: [UserService ],
    useFactory:usersDetailsFactory
  }
];

export function usersDetailsFactory(userService:UserService
): Signal<User | null>{
  const userId = injectParams('id')();
    userService.executeGetUserById(userId as string);
    return userService.user;
}
