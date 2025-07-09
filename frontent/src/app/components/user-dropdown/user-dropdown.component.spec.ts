import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserDropdownComponent } from './user-dropdown.component';
import {AuthService} from '@core/services/auth.service';

describe('UserDropdownComponent', () => {
  let component: UserDropdownComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let fixture: ComponentFixture<UserDropdownComponent>;

  beforeEach(async () => {
    authServiceSpy= jasmine.createSpyObj('AuthService',[
      'loggedUser',
    ]);
    await TestBed.configureTestingModule({
      imports: [UserDropdownComponent],
      providers:[
        {
          provide: AuthService,
          useValue: authServiceSpy,
        },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
