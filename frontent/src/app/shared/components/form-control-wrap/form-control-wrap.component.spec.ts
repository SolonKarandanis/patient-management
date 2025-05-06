import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormControlWrapComponent } from './form-control-wrap.component';

describe('FormControlWrapComponent', () => {
  let component: FormControlWrapComponent;
  let fixture: ComponentFixture<FormControlWrapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormControlWrapComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormControlWrapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
