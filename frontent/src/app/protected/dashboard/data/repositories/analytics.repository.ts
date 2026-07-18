import { Injectable } from '@angular/core';
import { httpResource, HttpResourceRef } from '@angular/common/http';
import { DailyEventCount, DailyPaymentSummary } from '@models/analytics.model';
import { ApiRepositories } from '@core/repositories/ApiRepositories';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsRepository {

  /**
   * Each factory must be called exactly once, synchronously, from an
   * injection context (a store's `withProps` factory) and the returned
   * `HttpResourceRef` held as a field. Calling it from inside a `withMethods`
   * handler would create a new, throwaway resource per invocation instead of
   * a stable, reactively-updating one.
   */
  getPatientDailySummaryResource(): HttpResourceRef<DailyEventCount[] | undefined> {
    return httpResource<DailyEventCount[]>(() => ({
      url: `${ApiRepositories.ANALYTICS}/patients/daily-summary`,
    }));
  }

  getUserDailySummaryResource(): HttpResourceRef<DailyEventCount[] | undefined> {
    return httpResource<DailyEventCount[]>(() => ({
      url: `${ApiRepositories.ANALYTICS}/users/daily-summary`,
    }));
  }

  getPaymentDailySummaryResource(): HttpResourceRef<DailyPaymentSummary[] | undefined> {
    return httpResource<DailyPaymentSummary[]>(() => ({
      url: `${ApiRepositories.ANALYTICS}/payments/daily-summary`,
    }));
  }
}
