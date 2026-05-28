import { inject, Injectable } from '@angular/core';
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';
import {KEYCLOAK_CLIENT_ID, KEYCLOAK_ISSUER, IS_PRODUCTION} from '@core/token';

@Injectable({ providedIn: 'root' })
export class OAuthConfigService {
  private readonly oauthService = inject(OAuthService);
  private readonly keycloakUrl = inject(KEYCLOAK_ISSUER);
  private readonly keycloakClientId = inject(KEYCLOAK_CLIENT_ID);
  private readonly isProd = inject(IS_PRODUCTION);

  private readonly authConfig: AuthConfig = {
    issuer: this.keycloakUrl,
    redirectUri: window.location.origin,
    postLogoutRedirectUri: window.location.origin,
    clientId: this.keycloakClientId,
    responseType: 'code',
    scope: 'openid profile email',
    showDebugInformation: !this.isProd,
    clearHashAfterLogin: true,
  };

  async initializeAndTryLogin(): Promise<void> {
    this.oauthService.configure(this.authConfig);
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();
    this.oauthService.setupAutomaticSilentRefresh();
  }

  login(): void {
    this.oauthService.initCodeFlow();
  }

  logout(): void {
    this.oauthService.revokeTokenAndLogout();
  }

  getAccessToken(): string | null {
    return this.oauthService.getAccessToken() || null;
  }

  isAuthenticated(): boolean {
    return this.oauthService.hasValidAccessToken();
  }
}
