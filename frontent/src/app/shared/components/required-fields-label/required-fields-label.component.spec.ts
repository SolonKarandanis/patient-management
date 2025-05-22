import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequiredFieldsLabelComponent } from './required-fields-label.component';

describe('RequiredFieldsLabelComponent', () => {
  let component: RequiredFieldsLabelComponent;
  let fixture: ComponentFixture<RequiredFieldsLabelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequiredFieldsLabelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RequiredFieldsLabelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
