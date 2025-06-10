import {computed, InjectionToken, Provider, signal, Signal} from '@angular/core';
import {UserService} from '../data/services/user.service';
import {User} from '@models/user.model';
import {injectParams} from '@shared/utils/injectParams';

export interface UserDetailsVM{
  user:User|null;
  loading:boolean;
}

export const USERS_DETAILS = new InjectionToken<Signal<UserDetailsVM| undefined>>(
  'A stream with selected user information',
);


export const USER_DETAILS_PROVIDERS: Provider[] =[
  {
    provide: USERS_DETAILS,
    deps: [UserService ],
    useFactory:usersDetailsFactory
  }
];

export function usersDetailsFactory(userService:UserService
): Signal<UserDetailsVM| undefined>{
  const userId = injectParams('id')();
  if(typeof userId==='string'){
    userService.executeGetUserById(userId);
    return computed(() => {
      const loading = userService.isLoading();
      const user = userService.user();
      return {
        user,
        loading
      }
    })
  }
  return signal(undefined);

  // return toSignal(params.paramMap.pipe(
  //   map((paramMap:ParamMap)=>{
  //     const publicId = paramMap.get('id');
  //     userService.executeGetUserById(publicId as string);
  //     const loading = userService.isLoading();
  //     const user = userService.user();
  //     return {
  //       user,
  //       loading
  //     }
  //   })
  // ));
}
