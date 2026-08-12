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
import { HlmToaster } from '@components/ui/sonner';
import { LoaderComponent } from '@components/loader/loader.component';
import { CommonEntitiesService } from '@core/services/common-entities.service';
import { NotificationService } from '@core/services/notification.service';
import { AuthService } from '@core/services/auth.service';
import { Subscription } from 'rxjs';
import { ChatbotComponent } from '@components/chatbot/chatbot.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HlmToaster, LoaderComponent, ChatbotComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('outlet')
  private outlet!: RouterOutlet;

  private readonly translate = inject(TranslateService);
  private readonly languageService = inject(LanguageService);
  private readonly commonEntitiesService = inject(CommonEntitiesService);
  protected route = inject(ActivatedRoute);
  private readonly notificationService = inject(NotificationService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly elementRef = inject(ElementRef);
  private routerEventsSubscription!: Subscription;

  protected isLoggedIn = this.authService.isLoggedIn;
  private isWebsocketsEnabled = this.commonEntitiesService.isWebSocketsEnabled;

  // appInitializer (app-initializer.ts) already awaits initializePublicApplicationConfig()
  // to completion before the app bootstraps. This effect's first run always sees
  // isLoggedIn() as false (the OAuth2/JWT check is still async at that point), so calling
  // initializePublicApplicationConfig() unconditionally on that first run re-triggers the
  // same call redundantly, right as the login check resolves and fires
  // initializeCommonEntities() a moment later. Both rxMethods hit the same
  // CommonEntitiesLookupStore concurrently in that window, and the resulting forkJoin never
  // resolves (reproduced: every constituent HTTP call completes individually, but forkJoin's
  // own next/complete never fires) — leaving the full-screen loader stuck on. Only call
  // initializePublicApplicationConfig() for a genuine post-login logout (session expiry),
  // not on the initial not-yet-authenticated state app-initializer already handled.
  private hasEverBeenLoggedIn = false;

  constructor() {
    this.authService.initAuth();
    effect(() => {
      if (this.isLoggedIn()) {
        this.hasEverBeenLoggedIn = true;
        this.commonEntitiesService.initializeCommonEntities();
      } else if (this.hasEverBeenLoggedIn) {
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
