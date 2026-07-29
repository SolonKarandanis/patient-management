import {ResolveFn} from '@angular/router';
import {inject} from '@angular/core';
import {User} from '@models/user.model';
import {UserService} from '../data';

export const userDetailsResolver: ResolveFn<User> = (route) =>
  inject(UserService).loadUserById$(route.paramMap.get('id')!);
