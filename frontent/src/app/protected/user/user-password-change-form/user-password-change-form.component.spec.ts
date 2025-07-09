import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPasswordChangeFormComponent } from './user-password-change-form.component';

xdescribe('UserPasswordChangeFormComponent', () => {
  let component: UserPasswordChangeFormComponent;
  let fixture: ComponentFixture<UserPasswordChangeFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserPasswordChangeFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserPasswordChangeFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
