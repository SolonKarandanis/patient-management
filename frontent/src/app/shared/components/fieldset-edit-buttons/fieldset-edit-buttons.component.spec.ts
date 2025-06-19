import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldsetEditButtonsComponent } from './fieldset-edit-buttons.component';

describe('FieldsetEditButtonsComponent', () => {
  let component: FieldsetEditButtonsComponent;
  let fixture: ComponentFixture<FieldsetEditButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FieldsetEditButtonsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FieldsetEditButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
