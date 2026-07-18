import {AnalyticsStore} from './analytics.store';
import {UtilService} from '@core/services/util.service';
import {ApplicationRef} from '@angular/core';
import {TestBed} from '@angular/core/testing';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';
import {TranslateLoader, TranslateModule, TranslateNoOpLoader} from '@ngx-translate/core';
import {DailyEventCount, DailyPaymentSummary} from '@models/analytics.model';

type AnalyticsStore = InstanceType<typeof AnalyticsStore>;

describe('AnalyticsStore', () => {
  let store: AnalyticsStore;
  let httpTesting: HttpTestingController;
  let appRef: ApplicationRef;
  let utilServiceSpy: jasmine.SpyObj<UtilService>;

  const apiUrl: string = 'analytics';

  const mockPatientsDailySummary: DailyEventCount[] = [
    {eventDate: '2026-01-01', eventType: 'CREATED', totalEvents: 3},
  ];
  const mockUserDailySummary: DailyEventCount[] = [
    {eventDate: '2026-01-01', eventType: 'REGISTERED', totalEvents: 1},
  ];
  const mockPaymentDailySummary: DailyPaymentSummary[] = [
    {eventDate: '2026-01-01', state: 'PAID', totalPayments: 2, totalAmount: 199.98},
  ];

  beforeEach(() => {
    utilServiceSpy = jasmine.createSpyObj('UtilService', ['showMessage']);

    TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot({
          loader: {provide: TranslateLoader, useClass: TranslateNoOpLoader}
        })
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {provide: UtilService, useValue: utilServiceSpy},
      ],
    });

    httpTesting = TestBed.inject(HttpTestingController);
    appRef = TestBed.inject(ApplicationRef);
    store = TestBed.inject(AnalyticsStore);

    // The store's resources are constructed above, but their loaders only
    // fire once Angular flushes pending effects.
    TestBed.tick();
  });

  afterEach(() => {
    httpTesting.verify();
  });

  function flushInitialRequests(
    patients: DailyEventCount[] = mockPatientsDailySummary,
    users: DailyEventCount[] = mockUserDailySummary,
    payments: DailyPaymentSummary[] = mockPaymentDailySummary,
  ): void {
    httpTesting.expectOne(`${apiUrl}/patients/daily-summary`).flush(patients);
    httpTesting.expectOne(`${apiUrl}/users/daily-summary`).flush(users);
    httpTesting.expectOne(`${apiUrl}/payments/daily-summary`).flush(payments);
  }

  it('should be created', async () => {
    expect(store).toBeTruthy();

    flushInitialRequests();
    await appRef.whenStable();
  });

  it('should load all 3 summaries eagerly and expose them once resolved', async () => {
    flushInitialRequests();
    await appRef.whenStable();

    expect(store.patientsDailySummary()).toEqual(mockPatientsDailySummary);
    expect(store.userDailySummary()).toEqual(mockUserDailySummary);
    expect(store.paymentDailySummary()).toEqual(mockPaymentDailySummary);
    expect(store.loading()).toBeFalse();
  });

  it('should track loading independently per summary', async () => {
    // Before any request resolves, everything is loading.
    expect(store.patientsDailySummaryLoading()).toBeTrue();
    expect(store.userDailySummaryLoading()).toBeTrue();
    expect(store.paymentDailySummaryLoading()).toBeTrue();

    // `appRef.whenStable()` only resolves once ALL pending resources settle,
    // so with 2 of 3 requests still outstanding, use a synchronous `tick()`
    // to observe the intermediate state instead.
    httpTesting.expectOne(`${apiUrl}/patients/daily-summary`).flush(mockPatientsDailySummary);
    await Promise.resolve();
    TestBed.tick();

    // Resolving one summary must not affect the other two still in flight.
    expect(store.patientsDailySummaryLoading()).toBeFalse();
    expect(store.userDailySummaryLoading()).toBeTrue();
    expect(store.paymentDailySummaryLoading()).toBeTrue();

    httpTesting.expectOne(`${apiUrl}/users/daily-summary`).flush(mockUserDailySummary);
    httpTesting.expectOne(`${apiUrl}/payments/daily-summary`).flush(mockPaymentDailySummary);
    await appRef.whenStable();

    expect(store.loading()).toBeFalse();
  });

  it('should surface an error toast and error state when a summary request fails, without affecting the others', async () => {
    httpTesting.expectOne(`${apiUrl}/patients/daily-summary`)
      .flush('failure', {status: 500, statusText: 'Internal Server Error'});
    httpTesting.expectOne(`${apiUrl}/users/daily-summary`).flush(mockUserDailySummary);
    httpTesting.expectOne(`${apiUrl}/payments/daily-summary`).flush(mockPaymentDailySummary);

    await appRef.whenStable();

    expect(store.patientsDailySummaryStatus()).toBe('error');
    expect(store.patientsDailySummaryError()).toBeTruthy();
    expect(store.userDailySummaryStatus()).toBe('loaded');
    expect(store.paymentDailySummaryStatus()).toBe('loaded');
    expect(utilServiceSpy.showMessage).toHaveBeenCalledWith('error', jasmine.any(String));
  });

  it('should reload a summary on demand via refresh', async () => {
    flushInitialRequests();
    await appRef.whenStable();

    store.refreshPatientDailySummary();
    TestBed.tick();

    const reloadedSummary: DailyEventCount[] = [
      {eventDate: '2026-01-02', eventType: 'CREATED', totalEvents: 5},
    ];
    httpTesting.expectOne(`${apiUrl}/patients/daily-summary`).flush(reloadedSummary);
    await appRef.whenStable();

    expect(store.patientsDailySummary()).toEqual(reloadedSummary);
  });
});
