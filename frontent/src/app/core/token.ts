import { InjectionToken } from '@angular/core';
import {environment} from '../../environments/environment';


export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  factory: (): string => environment.backEndDomain,
  providedIn: 'root',
});
