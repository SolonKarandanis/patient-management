import {computed, effect, inject, untracked} from '@angular/core';
import {signalStore, withComputed, withHooks, withMethods, withProps} from '@ngrx/signals';
import {UtilService} from '@core/services/util.service';
import {TranslateService} from '@ngx-translate/core';
import {AnalyticsRepository} from '../repositories/analytics.repository';
import {prefixedCallState, resourceCallState} from '@core/store/features/resource-call-state';
import {DailyEventCount, DailyPaymentSummary} from '@models/analytics.model';

// Stable, shared fallback references — a fresh `[]` literal inside a computed()
// is a new object identity on every evaluation, which Angular's default
// reference-equality check treats as "changed" even when the data is
// semantically identical, triggering unnecessary downstream re-evaluation.
const EMPTY_DAILY_EVENT_COUNTS: DailyEventCount[] = [];
const EMPTY_DAILY_PAYMENT_SUMMARY: DailyPaymentSummary[] = [];

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
    // httpResource().value() throws (ResourceValueError) when read while the
    // resource is in an error state with no prior successful value — hasValue()
    // is the documented guard against that, not just a defensive nicety.
    patientsDailySummary: computed(() => patientsResource.hasValue() ? patientsResource.value() : EMPTY_DAILY_EVENT_COUNTS),
    userDailySummary: computed(() => usersResource.hasValue() ? usersResource.value() : EMPTY_DAILY_EVENT_COUNTS),
    paymentDailySummary: computed(() => paymentsResource.hasValue() ? paymentsResource.value() : EMPTY_DAILY_PAYMENT_SUMMARY),

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
          // Sonner's toast.error() reads its own internal `toasts` signal
          // (to dedupe by id) and then writes to it. Angular's dependency
          // tracking is dynamically scoped to whatever's synchronously
          // executing when a signal is read — so without untracked(), that
          // read happening inside this effect's call stack silently
          // subscribes the effect to Sonner's signal too, and the write
          // right after it re-triggers this same effect. Since the error
          // condition above is still true, it calls showMessage() again,
          // which reads+writes the toast signal again — an infinite
          // self-retriggering loop that blocks the main thread. untracked()
          // stops any signal access inside the callback from being counted
          // as a dependency of this effect.
          untracked(() => {
            utilService.showMessage('error', translate.instant('SEARCH.ERRORS.search-failed'));
          });
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
