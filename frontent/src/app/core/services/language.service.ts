import { Injectable } from '@angular/core';
import {ApplicationLanguageStorageKey, DefaultApplicationLanguage} from '@models/constants';
import {DefaultLangChangeEvent, TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  selectedLanguageIso:string = DefaultApplicationLanguage;
  constructor(private readonly translateService: TranslateService) {
    this.translateService.onDefaultLangChange.subscribe((event:DefaultLangChangeEvent)=>{
      this.selectedLanguageIso=event.lang;
      localStorage.setItem(ApplicationLanguageStorageKey,this.selectedLanguageIso);
    });
  }

  changeLanguage(targetLangIso: string): void {
    this.translateService.use(targetLangIso);
    this.selectedLanguageIso = targetLangIso;
  }
}
