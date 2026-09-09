import { inject, Service } from '@angular/core';
import {ApplicationLanguageStorageKey, DefaultApplicationLanguage} from '@models/constants';
import {LangChangeEvent, TranslateService} from '@ngx-translate/core';

@Service()
export class LanguageService {
  private readonly translateService = inject(TranslateService);

  selectedLanguageIso:string = DefaultApplicationLanguage;
  constructor() {
    this.translateService.onLangChange.subscribe((event:LangChangeEvent)=>{
      this.selectedLanguageIso=event.lang;
      localStorage.setItem(ApplicationLanguageStorageKey,this.selectedLanguageIso);
    });
  }

  changeLanguage(targetLangIso: string): void {
    this.translateService.use(targetLangIso);
    this.selectedLanguageIso = targetLangIso;
  }
}
