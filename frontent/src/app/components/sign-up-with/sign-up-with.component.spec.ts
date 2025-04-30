import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SignUpWithComponent } from './sign-up-with.component';

describe('SignUpWithComponent', () => {
  let component: SignUpWithComponent;
  let fixture: ComponentFixture<SignUpWithComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignUpWithComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SignUpWithComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
