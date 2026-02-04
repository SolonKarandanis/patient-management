import {DailyEventCount, DailyPaymentSummary} from '@models/analytics.model';

export type AnalyticsState ={
  readonly patientsDailySummary: DailyEventCount[];
  readonly userDailySummary: DailyEventCount[];
  readonly paymentDailySummary: DailyPaymentSummary[];
}

export const initialAnalyticsState: AnalyticsState ={
  patientsDailySummary:[],
  userDailySummary:[],
  paymentDailySummary:[],
}
