import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientsDailySummaryComponent } from './patients-daily-summary.component';

describe('PatientsDailySummaryComponent', () => {
  let component: PatientsDailySummaryComponent;
  let fixture: ComponentFixture<PatientsDailySummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientsDailySummaryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientsDailySummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
