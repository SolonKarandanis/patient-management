import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Errors404Component } from './errors404.component';

describe('Errors404Component', () => {
  let component: Errors404Component;
  let fixture: ComponentFixture<Errors404Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Errors404Component]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Errors404Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
