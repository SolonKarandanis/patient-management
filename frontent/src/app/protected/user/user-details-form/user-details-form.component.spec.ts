import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserDetailsFormComponent } from './user-details-form.component';

xdescribe('UserDetailsFormComponent', () => {
  let component: UserDetailsFormComponent;
  let fixture: ComponentFixture<UserDetailsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserDetailsFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
