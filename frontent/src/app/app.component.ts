import {Component, inject, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import {LanguageService} from '@core/services/language.service';
import {PrimeNG} from 'primeng/config';
import {ToastModule} from 'primeng/toast';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    ToastModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  private readonly translate = inject(TranslateService);
  private readonly languageService = inject(LanguageService);
  private readonly primengConfig = inject(PrimeNG);

  ngOnInit(): void {
    this.translate.setDefaultLang(this.languageService.selectedLanguageIso);
    this.translate.use(this.languageService.selectedLanguageIso);
    this.primengConfig.ripple.set(true);
  }
}
