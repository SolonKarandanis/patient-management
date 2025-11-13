import {Component, effect, inject, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, RouterOutlet} from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import {LanguageService} from '@core/services/language.service';
import {PrimeNG} from 'primeng/config';
import {ToastModule} from 'primeng/toast';
import {routeTransition} from './route-transition';
import {LoaderComponent} from '@components/loader/loader.component';
import {CommonEntitiesService} from '@core/services/common-entities.service';
import {NotificationService} from '@core/services/notification.service';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    ToastModule,
    LoaderComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  animations:[ routeTransition]
})
export class AppComponent implements OnInit,OnDestroy{
  private readonly translate = inject(TranslateService);
  private readonly languageService = inject(LanguageService);
  private readonly commonEntitiesService = inject(CommonEntitiesService);
  private readonly primengConfig = inject(PrimeNG);
  protected route = inject(ActivatedRoute);
  private readonly notificationService = inject(NotificationService);

  constructor() {
    effect(() => {
      this.commonEntitiesService.initializeCommonEntities();
      this.notificationService.connect();
    });
  }

  ngOnInit(): void {
    this.translate.setDefaultLang(this.languageService.selectedLanguageIso);
    this.translate.use(this.languageService.selectedLanguageIso);
    this.primengConfig.ripple.set(true);
  }

  ngOnDestroy(): void {
    this.notificationService.disconnect();
  }
}
