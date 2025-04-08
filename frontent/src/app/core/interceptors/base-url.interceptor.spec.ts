import { HttpClient, HttpRequest, HttpStatusCode, provideHttpClient } from '@angular/common/http';
import { BaseUrlInterceptor } from './base-url.interceptor';
import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';

xdescribe('BaseUrlInterceptor', () =>{
  let interceptor: BaseUrlInterceptor;
  let mockReq: HttpRequest<any>;
  let requestCloneSpy: jasmine.Spy;

  const mockUrl: string = 'test-url';
  const mockAssetsUrl: string = '/assets/i18n';
  const webApiEndpoint: string = 'http://localhost:8080/airbnb/v1';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BaseUrlInterceptor, provideHttpClient(), provideHttpClientTesting(), HttpClient],
    });

    interceptor = TestBed.inject(BaseUrlInterceptor);
    // mockReq = new HttpRequest('GET', mockUrl);
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should add base URL to all HTTP requests', (done) => {
    mockReq = new HttpRequest('GET', mockUrl);

    requestCloneSpy = spyOn(mockReq, 'clone');
    requestCloneSpy.and.callThrough();

    const next: any = {
      handle: (req: HttpRequest<any>) => {
        expect(req.url).toBe(`${webApiEndpoint}/${mockUrl}`);
        return of({ status: HttpStatusCode.Ok } as Response);
      },
    };

    interceptor.intercept(mockReq, next).subscribe(() => {
      expect(requestCloneSpy).toHaveBeenCalled();
      done();
    });
  });

  it('should leave i18n URL alone', (done) => {
    mockReq = new HttpRequest('GET', mockAssetsUrl);

    requestCloneSpy = spyOn(mockReq, 'clone');
    requestCloneSpy.and.callThrough();

    const next: any = {
      handle: (req: HttpRequest<any>) => {
        expect(req.url).toBe(`${mockAssetsUrl}`);
        return of({ status: HttpStatusCode.Ok } as Response);
      },
    };

    interceptor.intercept(mockReq, next).subscribe(() => {
      expect(requestCloneSpy).not.toHaveBeenCalled();
      done();
    });
  });
})
