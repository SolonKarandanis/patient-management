import { InjectionToken, Provider, Signal} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {UserService} from '../data/services/user.service';
import {map} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {User} from '@models/user.model';

interface UserDetailsVM{
  user:User|null;
  loading:boolean;
}

export const USERS_DETAILS = new InjectionToken<Signal<UserDetailsVM| undefined>>(
  'A stream with selected user information',
);


export const USER_DETAILS_PROVIDERS: Provider[] =[
  {
    provide: USERS_DETAILS,
    deps: [ActivatedRoute,UserService ],
    useFactory:usersDetailsFactory
  }
];

export function usersDetailsFactory(
  params: ActivatedRoute,userService:UserService
): Signal<UserDetailsVM| undefined>{
  return toSignal(params.paramMap.pipe(
    map((paramMap:ParamMap)=>{
      const publicId = paramMap.get('id');
      userService.executeGetUserById(publicId as string);
      const loading = userService.isLoading();
      const user = userService.user();
      return {
        user,
        loading
      }
    })
  ));
}
