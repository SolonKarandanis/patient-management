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

  public executeGetPatientsDailySummary():void{
    this.analyticsStore.getPatientDailySummary();
  }

  public executeGetUserDailySummary():void{
    this.analyticsStore.getUserDailySummary();
  }

  public executeGetPaymentDailySummary():void{
    this.analyticsStore.getPaymentDailySummary();
  }
}
