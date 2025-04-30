import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Errors500Component } from './errors500.component';

describe('Errors500Component', () => {
  let component: Errors500Component;
  let fixture: ComponentFixture<Errors500Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Errors500Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Errors500Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
