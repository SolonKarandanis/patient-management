import { InjectionToken } from '@angular/core';
import {environment} from '../../environments/environment';

export const IS_PRODUCTION = new InjectionToken<boolean>('IS_PRODUCTION', {
  factory: (): boolean => environment.production,
  providedIn: 'root',
});

export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  factory: (): string => environment.backEndDomain,
  providedIn: 'root',
});

export const WS_BASE_URL = new InjectionToken<string>('WS_BASE_URL', {
  factory: (): string => environment.webSocketDomain,
  providedIn: 'root',
});

export const KEYCLOAK_ISSUER = new InjectionToken<string>('KEYCLOAK_ISSUER', {
  factory: (): string => environment.keycloakIssuer,
  providedIn: 'root',
});

export const KEYCLOAK_CLIENT_ID = new InjectionToken<string>('KEYCLOAK_CLIENT_ID', {
  factory: (): string => environment.keycloakClientId,
  providedIn: 'root',
});
