import { inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { CommonEntitiesRepository } from '@core/repositories/common-entities.repository';
import { AuthModeService } from '@core/services/auth-mode.service';
import { OAuthConfigService } from '@core/services/oauth-config.service';

export async function appInitializer(): Promise<void> {
  const commonEntitiesRepo = inject(CommonEntitiesRepository);
  const authModeService = inject(AuthModeService);
  const oauthConfigService = inject(OAuthConfigService);

  try {
    const config = await firstValueFrom(commonEntitiesRepo.getPublicApplicationConfig());
    const mode = (config.AUTH_MODE as 'jwt' | 'oauth2') ?? 'jwt';
    authModeService.setMode(mode);
    if (mode === 'oauth2') {
      await oauthConfigService.initializeAndTryLogin();
    }
  } catch {
    authModeService.setMode('jwt');
  }
}
