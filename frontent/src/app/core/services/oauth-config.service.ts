import { inject, Injectable } from '@angular/core';
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OAuthConfigService {
  private readonly oauthService = inject(OAuthService);

  private readonly authConfig: AuthConfig = {
    issuer: environment.keycloakIssuer,
    redirectUri: window.location.origin,
    clientId: environment.keycloakClientId,
    responseType: 'code',
    scope: 'openid profile email',
    showDebugInformation: !environment.production,
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
