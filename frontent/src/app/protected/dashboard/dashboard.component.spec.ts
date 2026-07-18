import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ConfirmationService, MessageService } from 'primeng/api';
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
        // requests) and UtilService's MessageService/ConfirmationService
        // (for the error-toast effect) as soon as this component is created.
        provideHttpClient(),
        provideHttpClientTesting(),
        MessageService,
        ConfirmationService,
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
});
