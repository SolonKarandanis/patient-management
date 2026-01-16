import {
  Component,
  effect,
  inject,
  OnDestroy,
  OnInit,
  ElementRef,
  ViewChild,
  AfterViewInit,
} from '@angular/core';
import {
  ActivatedRoute,
  RouterOutlet,
  Router,
  NavigationStart,
  NavigationEnd,
  NavigationCancel,
  NavigationError,
} from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { LanguageService } from '@core/services/language.service';
import { PrimeNG } from 'primeng/config';
import { ToastModule } from 'primeng/toast';
import { LoaderComponent } from '@components/loader/loader.component';
import { CommonEntitiesService } from '@core/services/common-entities.service';
import { NotificationService } from '@core/services/notification.service';
import { AuthService } from '@core/services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastModule, LoaderComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('outlet')
  private outlet!: RouterOutlet;

  private readonly translate = inject(TranslateService);
  private readonly languageService = inject(LanguageService);
  private readonly commonEntitiesService = inject(CommonEntitiesService);
  private readonly primengConfig = inject(PrimeNG);
  protected route = inject(ActivatedRoute);
  private readonly notificationService = inject(NotificationService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly elementRef = inject(ElementRef);
  private routerEventsSubscription!: Subscription;

  private isLoggedIn = this.authService.isLoggedIn;
  private isWebsocketsEnabled = this.commonEntitiesService.isWebSocketsEnabled;

  constructor() {
    this.authService.initAuth();
    effect(() => {
      if (this.isLoggedIn()) {
        this.commonEntitiesService.initializeCommonEntities();
      } else {
        this.commonEntitiesService.initializePublicApplicationConfig();
      }
    });
    effect((onCleanup) => {
      if (this.isWebsocketsEnabled()) {
        this.notificationService.connect();
        onCleanup(() => {
          this.notificationService.disconnect();
        });
      }
    });
  }

  ngOnInit(): void {
    this.translate.setDefaultLang(this.languageService.selectedLanguageIso);
    this.translate.use(this.languageService.selectedLanguageIso);
    this.primengConfig.ripple.set(true);
  }

  ngAfterViewInit(): void {
    this.routerEventsSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.elementRef.nativeElement.classList.add('ng-animating');
        this.elementRef.nativeElement.classList.add('animate-leave');
      } else if (
        event instanceof NavigationEnd ||
        event instanceof NavigationCancel ||
        event instanceof NavigationError
      ) {
        this.elementRef.nativeElement.classList.remove('animate-leave');
        this.elementRef.nativeElement.classList.add('animate-enter');
        setTimeout(() => {
          this.elementRef.nativeElement.classList.remove('ng-animating');
          this.elementRef.nativeElement.classList.remove('animate-enter');
        }, 1000);
      }
    });
  }

  ngOnDestroy(): void {
    this.routerEventsSubscription.unsubscribe();
  }
}
