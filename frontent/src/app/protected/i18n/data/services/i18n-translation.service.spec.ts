import { TestBed } from '@angular/core/testing';

import { I18nTranslationService } from './i18n-translation.service';

describe('I18nTranslationService', () => {
  let service: I18nTranslationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(I18nTranslationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
