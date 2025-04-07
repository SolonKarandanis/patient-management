import {AuthRepository} from './auth.repository';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {mockJwt, mockLoginCredentials, mockUser} from '@testing/mockData';
import {JwtDTO} from '@models/auth.model';
import {User} from '@models/user.model';

describe('AuthRepository', () =>{
  let repository: AuthRepository;
  let httpTesting: HttpTestingController;
  const loginApiUrl: string = 'authenticate';
  const usersApiUrl: string = 'users';

  beforeEach(() =>{

    TestBed.configureTestingModule({
      providers:[
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    repository = TestBed.inject(AuthRepository);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(repository).toBeTruthy();
  });

  it('should perform login', () =>{
    repository.login(mockLoginCredentials).subscribe({
      next: (result: JwtDTO) =>{
        expect(result).toBeTruthy();
        expect(result).toEqual(mockJwt);
      }
    });

    const req = httpTesting.expectOne(`${loginApiUrl}`, 'Request perform login request');

    expect(req.request.method).toBe('POST');
    expect(req.request.params.keys().length).toBe(0);

    req.flush(mockJwt);
  });

  it('should get logged in users account', () =>{
    repository.getUserByToken().subscribe({
      next: (result: User) =>{
        expect(result).toBeTruthy();
        expect(result).toEqual(mockUser);
      }
    });

    const req = httpTesting.expectOne(`${usersApiUrl}/account`, 'Request for the logged in users account');

    expect(req.request.method).toBe('GET');
    expect(req.request.params.keys().length).toBe(0);

    req.flush(mockUser);
  });
});
