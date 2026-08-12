import { ComponentFixture, fakeAsync, tick, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslateLoader, TranslateModule, TranslateNoOpLoader } from '@ngx-translate/core';

import { DashboardComponent } from './dashboard.component';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DashboardComponent,
        TranslateModule.forRoot({
          loader: {provide: TranslateLoader, useClass: TranslateNoOpLoader}
        }),
      ],
      providers: [
        // AnalyticsStore's resources load eagerly as soon as it's first
        // injected, which pulls in the real HttpClient (for the analytics
        // requests) as soon as this component is created. UtilService's
        // ToastService/ConfirmService dependencies (for the error-toast
        // effect) are providedIn: 'root' and resolve without extra setup.
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();

    httpTesting.expectOne('analytics/patients/daily-summary').flush([]);
    httpTesting.expectOne('analytics/users/daily-summary').flush([]);
    httpTesting.expectOne('analytics/payments/daily-summary').flush([]);
  });

  it('settles into an error state without hanging when analytics requests 500', fakeAsync(() => {
    httpTesting.expectOne('analytics/patients/daily-summary')
      .flush('boom', { status: 500, statusText: 'Internal Server Error' });
    httpTesting.expectOne('analytics/users/daily-summary')
      .flush('boom', { status: 500, statusText: 'Internal Server Error' });
    httpTesting.expectOne('analytics/payments/daily-summary')
      .flush('boom', { status: 500, statusText: 'Internal Server Error' });

    // If AnalyticsStore's error-toast effect enters a synchronous re-trigger
    // loop, this tick() (and the whole test) hangs instead of returning —
    // that hang is itself the repro signal, not just the assertions below.
    tick(1000);
    fixture.detectChanges();

    expect(component.patientsDailySummaryError()).toBeTruthy();
    expect(component.userDailySummaryError()).toBeTruthy();
    expect(component.paymentDailySummaryError()).toBeTruthy();
  }));
});
