import {AnalyticsRepository} from './analytics.repository';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {ApplicationRef} from '@angular/core';
import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {DailyEventCount, DailyPaymentSummary} from '@models/analytics.model';

describe('AnalyticsRepository', () => {
  let repository: AnalyticsRepository;
  let httpTesting: HttpTestingController;
  let appRef: ApplicationRef;

  const apiUrl: string = 'analytics';

  const mockDailyEventCounts: DailyEventCount[] = [
    {eventDate: '2026-01-01', eventType: 'CREATED', totalEvents: 3},
  ];

  const mockDailyPaymentSummary: DailyPaymentSummary[] = [
    {eventDate: '2026-01-01', state: 'PAID', totalPayments: 2, totalAmount: 199.98},
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    repository = TestBed.inject(AnalyticsRepository);
    httpTesting = TestBed.inject(HttpTestingController);
    appRef = TestBed.inject(ApplicationRef);
  });

  /**
   * `httpResource()` must be constructed within an injection context (NG0203
   * otherwise), and its loader only fires once Angular flushes pending
   * effects — neither of which happens implicitly inside a plain `it()` body.
   */
  function createResource<T>(factory: () => T): T {
    const resource = TestBed.runInInjectionContext(factory);
    TestBed.tick();
    return resource;
  }

  it('should be created', () => {
    expect(repository).toBeTruthy();
  });

  it('should get the patient daily summary resource', async () => {
    const resource = createResource(() => repository.getPatientDailySummaryResource());

    const req = httpTesting.expectOne(`${apiUrl}/patients/daily-summary`, 'Request for patient daily summary');
    expect(req.request.method).toBe('GET');
    req.flush(mockDailyEventCounts);

    await appRef.whenStable();

    expect(resource.value()).toEqual(mockDailyEventCounts);
    expect(resource.status()).toBe('resolved');
    expect(resource.isLoading()).toBeFalse();
  });

  it('should get the user daily summary resource', async () => {
    const resource = createResource(() => repository.getUserDailySummaryResource());

    const req = httpTesting.expectOne(`${apiUrl}/users/daily-summary`, 'Request for user daily summary');
    expect(req.request.method).toBe('GET');
    req.flush(mockDailyEventCounts);

    await appRef.whenStable();

    expect(resource.value()).toEqual(mockDailyEventCounts);
  });

  it('should get the payment daily summary resource', async () => {
    const resource = createResource(() => repository.getPaymentDailySummaryResource());

    const req = httpTesting.expectOne(`${apiUrl}/payments/daily-summary`, 'Request for payment daily summary');
    expect(req.request.method).toBe('GET');
    req.flush(mockDailyPaymentSummary);

    await appRef.whenStable();

    expect(resource.value()).toEqual(mockDailyPaymentSummary);
  });

  it('should surface an error on the resource when the request fails', async () => {
    const resource = createResource(() => repository.getPatientDailySummaryResource());

    const req = httpTesting.expectOne(`${apiUrl}/patients/daily-summary`, 'Request for patient daily summary');
    req.flush('failure', {status: 500, statusText: 'Internal Server Error'});

    await appRef.whenStable();

    expect(resource.status()).toBe('error');
    expect(resource.error()).toBeTruthy();
  });
});
