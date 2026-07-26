import { inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { filter, firstValueFrom } from 'rxjs';
import { CommonEntitiesLookupStore } from '@core/store/common-entities/common-entities-lookup.store';
import { AuthModeService } from '@core/services/auth-mode.service';
import { OAuthConfigService } from '@core/services/oauth-config.service';

export async function appInitializer(): Promise<void> {
  const commonEntitiesLookupStore = inject(CommonEntitiesLookupStore);
  const authModeService = inject(AuthModeService);
  const oauthConfigService = inject(OAuthConfigService);
  const status$ = toObservable(commonEntitiesLookupStore.status);

  try {
    commonEntitiesLookupStore.initializePublicApplicationConfig();
    await firstValueFrom(status$.pipe(filter((status) => status === 'loaded' || status === 'error')));

    const mode = commonEntitiesLookupStore.authMode();
    authModeService.setMode(mode);
    if (mode === 'oauth2') {
      await oauthConfigService.initializeAndTryLogin();
    }
  } catch {
    authModeService.setMode('jwt');
  }
}
