import { TestBed } from '@angular/core/testing';

import { LanguageService } from './language.service';
import {TranslateService} from '@ngx-translate/core';
import {of} from 'rxjs';

describe('LanguageService', () => {
  let service: LanguageService;
  let translateSpy: jasmine.SpyObj<TranslateService>;

  beforeEach(() => {
    translateSpy = jasmine.createSpyObj('TranslateService', ['use']);
    TestBed.configureTestingModule({
      providers: [
        {
          provide: TranslateService,
          useValue: translateSpy,
        },
      ],
    });
    service = TestBed.inject(LanguageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should change Languages', () => {
    const obj = {'gr':'gr'}
    translateSpy.use.and.returnValue(of(obj));

    service.changeLanguage('gr');

    expect(translateSpy.use).toHaveBeenCalledWith('gr');
    expect(translateSpy.use).toHaveBeenCalledTimes(1);

    expect(service.selectedLanguageIso).toEqual('gr');
  });
});
