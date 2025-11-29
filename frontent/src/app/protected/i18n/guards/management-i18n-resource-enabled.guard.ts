import {CanActivateFn, Router} from '@angular/router';
import {UiService} from '@core/services/ui.service';
import {inject} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {handleOk, redirectToSafety} from '@core/guards/guards.utils';

export const manageI18nResourcesEnabledGuard: CanActivateFn = () => {
  const uiService: UiService = inject(UiService);
  const translate: TranslateService = inject(TranslateService);
  const commonEntitiesService: CommonEntitiesService = inject(CommonEntitiesService);
  const router: Router = inject(Router);
  uiService.showScreenLoaderWithMessage(translate.instant('GLOBAL.OTHER.verifyingAccess'));
  const isEnabled: boolean = commonEntitiesService.isManagementOfI18nResourcesEnabled();
  if (!isEnabled) {
    return redirectToSafety(router, uiService);
  }
  return handleOk(true, uiService);

}
