import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldsetHeaderWithButtonsComponent } from './fieldset-header-with-buttons.component';

xdescribe('FieldsetHeaderWithButtonsComponent', () => {
  let component: FieldsetHeaderWithButtonsComponent;
  let fixture: ComponentFixture<FieldsetHeaderWithButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FieldsetHeaderWithButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FieldsetHeaderWithButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
