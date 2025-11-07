import {ChangeDetectionStrategy, Component, effect, inject, OnDestroy} from '@angular/core';
import {AuthService} from '@core/services/auth.service';
import {NotificationService} from '@core/services/notification.service';

@Component({
  selector: 'app-protected',
  standalone:false,
  template: `
    <div>
      <app-sidebar></app-sidebar>
      <div class="relative md:ml-64 bg-blueGray-100">
        <app-header></app-header>
        <div class="px-4 md:px-10 mx-auto w-full -m-24">
          <div class="relative md:pt-32 pb-32 pt-12">
            <div class="min-h-[76vh]">
              <router-outlet></router-outlet>
            </div>
            <app-footer></app-footer>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrl: './protected.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProtectedComponent implements OnDestroy{
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private isConnected = false;

  constructor() {
    effect(() => {
      const isLoggedIn = this.authService.isLoggedIn();
      const userId = this.authService.loggedUserId();
      if (isLoggedIn && userId && !this.isConnected) {
        this.notificationService.connect('http://localhost:4010/ws', userId);
        this.isConnected = true;
      }
    });
  }

  ngOnDestroy(): void {
    this.notificationService.disconnect();
  }
}
