import { ComponentFixture, TestBed } from '@angular/core/testing';

import { I18nManagementComponent } from './i18n-management.component';

describe('I18nManagementComponent', () => {
  let component: I18nManagementComponent;
  let fixture: ComponentFixture<I18nManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [I18nManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(I18nManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
