import {Router, UrlTree} from '@angular/router';
import {UiService} from '@core/services/ui.service';

export const handleOk = (hasAccess: boolean, uiService: UiService): boolean => {
  uiService.hideScreenLoader();
  return hasAccess;
};

export const redirectToSafety = (router: Router, uiService: UiService): UrlTree => {
  uiService.hideScreenLoader();
  return getSafetyUrlTree(router);
};

export const getSafetyUrlTree = (router: Router): UrlTree => {
  return router.createUrlTree(['/unauthorized']);
};
