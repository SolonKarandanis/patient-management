import {GenericService} from '@core/services/generic.service';
import {inject, Injectable} from '@angular/core';
import {AnalyticsStore} from '../store/analytics.store';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService extends GenericService{
  private analyticsStore = inject(AnalyticsStore);

  public patientsDailySummary = this.analyticsStore.patientsDailySummary;
  public userDailySummary = this.analyticsStore.userDailySummary;
  public paymentDailySummary = this.analyticsStore.paymentDailySummary;

  public patientsDailySummaryLoading = this.analyticsStore.patientsDailySummaryLoading;
  public patientsDailySummaryError = this.analyticsStore.patientsDailySummaryError;
  public userDailySummaryLoading = this.analyticsStore.userDailySummaryLoading;
  public userDailySummaryError = this.analyticsStore.userDailySummaryError;
  public paymentDailySummaryLoading = this.analyticsStore.paymentDailySummaryLoading;
  public paymentDailySummaryError = this.analyticsStore.paymentDailySummaryError;

  /**
   * The store's resources load eagerly as soon as `AnalyticsStore` is first
   * injected, so these are only needed for an explicit manual refresh
   * (e.g. a refresh button), not for the initial load.
   */
  public refreshPatientsDailySummary():void{
    this.analyticsStore.refreshPatientDailySummary();
  }

  public refreshUserDailySummary():void{
    this.analyticsStore.refreshUserDailySummary();
  }

  public refreshPaymentDailySummary():void{
    this.analyticsStore.refreshPaymentDailySummary();
  }
}
