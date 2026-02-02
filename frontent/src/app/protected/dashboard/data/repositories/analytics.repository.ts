import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DailyEventCount, DailyPaymentSummary } from '../services/analytics.service';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsRepository {

  private baseUrl = '/api/analytics';

  constructor(private http: HttpClient) { }

  getPatientDailySummary(): Observable<DailyEventCount[]> {
    return this.http.get<DailyEventCount[]>(`${this.baseUrl}/patients/daily-summary`);
  }

  getUserDailySummary(): Observable<DailyEventCount[]> {
    return this.http.get<DailyEventCount[]>(`${this.baseUrl}/users/daily-summary`);
  }

  getPaymentDailySummary(): Observable<DailyPaymentSummary[]> {
    return this.http.get<DailyPaymentSummary[]>(`${this.baseUrl}/payments/daily-summary`);
  }
}
