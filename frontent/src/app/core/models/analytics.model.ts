export interface DailyEventCount{
  eventDate: string,
  eventType: string,
  totalEvents: number,
}

export interface DailyPaymentSummary{
  eventDate: string,
  state: string,
  totalPayments: number,
  totalAmount: number,
}

