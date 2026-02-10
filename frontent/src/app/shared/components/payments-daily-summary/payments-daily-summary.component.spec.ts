import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentsDailySummaryComponent } from './payments-daily-summary.component';

describe('PaymentsDailySummaryComponent', () => {
  let component: PaymentsDailySummaryComponent;
  let fixture: ComponentFixture<PaymentsDailySummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentsDailySummaryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaymentsDailySummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
