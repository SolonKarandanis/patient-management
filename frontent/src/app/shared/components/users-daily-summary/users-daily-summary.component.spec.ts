import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersDailySummaryComponent } from './users-daily-summary.component';

describe('UsersDailySummaryComponent', () => {
  let component: UsersDailySummaryComponent;
  let fixture: ComponentFixture<UsersDailySummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsersDailySummaryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsersDailySummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
