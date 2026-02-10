import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {AnalyticsService} from './data/service/analytics.service';
import {UsersDailySummaryComponent} from '@components/users-daily-summary/users-daily-summary.component';
import {PatientsDailySummaryComponent} from '@components/patients-daily-summary/patients-daily-summary.component';
import {PaymentsDailySummaryComponent} from '@components/payments-daily-summary/payments-daily-summary.component';

@Component({
  selector: 'app-dashboard',
  template: `
    <div class="flex flex-wrap">
      <div class="w-full xl:w-8/12 mb-12 xl:mb-0 px-4 text-black">
        <app-users-daily-summary [userDailySummary]="userDailySummary()"/>
        <app-patients-daily-summary [patientsDailySummary]="patientsDailySummary()" />
        <app-payments-daily-summary [paymentDailySummary]="paymentDailySummary()" />
      </div>
    </div>
  `,
  styleUrl: './dashboard.component.css',
  imports: [
    UsersDailySummaryComponent,
    PatientsDailySummaryComponent,
    PaymentsDailySummaryComponent
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  private analyticsService = inject(AnalyticsService);

  public patientsDailySummary = this.analyticsService.patientsDailySummary;
  public userDailySummary = this.analyticsService.userDailySummary;
  public paymentDailySummary = this.analyticsService.paymentDailySummary;


  ngOnInit(): void {
    this.analyticsService.executeGetPatientsDailySummary();
    this.analyticsService.executeGetUserDailySummary();
    this.analyticsService.executeGetPaymentDailySummary();
  }
}
