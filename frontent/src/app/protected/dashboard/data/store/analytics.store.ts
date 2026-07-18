import {computed, effect, inject} from '@angular/core';
import {signalStore, withComputed, withHooks, withMethods, withProps} from '@ngrx/signals';
import {UtilService} from '@core/services/util.service';
import {TranslateService} from '@ngx-translate/core';
import {AnalyticsRepository} from '../repositories/analytics.repository';
import {prefixedCallState, resourceCallState} from '@core/store/features/resource-call-state';

export const AnalyticsStore = signalStore(
  {providedIn:'root'},
  withProps(() => {
    const analyticsRepo = inject(AnalyticsRepository);
    return {
      analyticsRepo,
      utilService: inject(UtilService),
      translate: inject(TranslateService),
      patientsResource: analyticsRepo.getPatientDailySummaryResource(),
      usersResource: analyticsRepo.getUserDailySummaryResource(),
      paymentsResource: analyticsRepo.getPaymentDailySummaryResource(),
    };
  }),
  withComputed(({patientsResource, usersResource, paymentsResource}) => ({
    patientsDailySummary: computed(() => patientsResource.value() ?? []),
    userDailySummary: computed(() => usersResource.value() ?? []),
    paymentDailySummary: computed(() => paymentsResource.value() ?? []),

    // Per-field state: each read now has its own independent loading/error
    // signal, instead of sharing one callStateStatus slice across all 3.
    ...prefixedCallState('patientsDailySummary', resourceCallState(patientsResource)),
    ...prefixedCallState('userDailySummary', resourceCallState(usersResource)),
    ...prefixedCallState('paymentDailySummary', resourceCallState(paymentsResource)),

    // Convenience aggregate, matching the previous single combined flag.
    loading: computed(() =>
      patientsResource.isLoading() || usersResource.isLoading() || paymentsResource.isLoading()
    ),
  })),
  withHooks({
    onInit({patientsResource, usersResource, paymentsResource, utilService, translate}) {
      effect(() => {
        if (patientsResource.error() || usersResource.error() || paymentsResource.error()) {
          utilService.showMessage('error', translate.instant('SEARCH.ERRORS.search-failed'));
        }
      });
    },
  }),
  withMethods(({patientsResource, usersResource, paymentsResource}) => ({
    refreshPatientDailySummary(){
      patientsResource.reload();
    },
    refreshUserDailySummary(){
      usersResource.reload();
    },
    refreshPaymentDailySummary(){
      paymentsResource.reload();
    },
  })),
);
