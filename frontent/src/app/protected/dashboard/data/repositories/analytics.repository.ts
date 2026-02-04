import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {DailyEventCount, DailyPaymentSummary} from '@models/analytics.model';
import {ApiRepositories} from '@core/repositories/ApiRepositories';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsRepository {

  constructor(private http: HttpClient) { }

  getPatientDailySummary(): Observable<DailyEventCount[]> {
    return this.http.get<DailyEventCount[]>(`${ApiRepositories.ANALYTICS}/patients/daily-summary`);
  }

  getUserDailySummary(): Observable<DailyEventCount[]> {
    return this.http.get<DailyEventCount[]>(`${ApiRepositories.ANALYTICS}/users/daily-summary`);
  }

  getPaymentDailySummary(): Observable<DailyPaymentSummary[]> {
    return this.http.get<DailyPaymentSummary[]>(`${ApiRepositories.ANALYTICS}/payments/daily-summary`);
  }
}
