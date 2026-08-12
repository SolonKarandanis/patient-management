import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {AnalyticsService} from './data/service/analytics.service';
import {UsersDailySummaryComponent} from '@components/users-daily-summary/users-daily-summary.component';
import {PatientsDailySummaryComponent} from '@components/patients-daily-summary/patients-daily-summary.component';
import {PaymentsDailySummaryComponent} from '@components/payments-daily-summary/payments-daily-summary.component';

@Component({
  selector: 'app-dashboard',
  template: `
    <div class="flex flex-wrap">
      <div class="w-full xl:w-8/12 mb-12 xl:mb-0 px-4 text-black">
        <app-users-daily-summary
          [userDailySummary]="userDailySummary()"
          [loading]="userDailySummaryLoading()"
          [error]="userDailySummaryError()"
          (retry)="analyticsService.refreshUserDailySummary()"/>
        <app-patients-daily-summary
          [patientsDailySummary]="patientsDailySummary()"
          [loading]="patientsDailySummaryLoading()"
          [error]="patientsDailySummaryError()"
          (retry)="analyticsService.refreshPatientsDailySummary()"/>
        <app-payments-daily-summary
          [paymentDailySummary]="paymentDailySummary()"
          [loading]="paymentDailySummaryLoading()"
          [error]="paymentDailySummaryError()"
          (retry)="analyticsService.refreshPaymentDailySummary()"/>
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
export class DashboardComponent {
  protected analyticsService = inject(AnalyticsService);

  // AnalyticsStore's resources load as soon as the store is first injected
  // (i.e. right here), so no explicit trigger call is needed on init.
  public patientsDailySummary = this.analyticsService.patientsDailySummary;
  public userDailySummary = this.analyticsService.userDailySummary;
  public paymentDailySummary = this.analyticsService.paymentDailySummary;

  public patientsDailySummaryLoading = this.analyticsService.patientsDailySummaryLoading;
  public patientsDailySummaryError = this.analyticsService.patientsDailySummaryError;
  public userDailySummaryLoading = this.analyticsService.userDailySummaryLoading;
  public userDailySummaryError = this.analyticsService.userDailySummaryError;
  public paymentDailySummaryLoading = this.analyticsService.paymentDailySummaryLoading;
  public paymentDailySummaryError = this.analyticsService.paymentDailySummaryError;
}
