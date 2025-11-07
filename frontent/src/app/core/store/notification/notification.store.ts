import {patchState, signalStore, withHooks, withMethods, withProps, withState} from '@ngrx/signals';
import { inject } from '@angular/core';
import { NotificationService } from '../../services/notification.service';
import {initialNotificationState, NotificationState} from '@core/store/notification/notification.state';
import {UtilService} from '@core/services/util.service';
import {Notification} from '@models/notification.model';
import {AuthService} from '@core/services/auth.service';


export const NotificationStore = signalStore(
  {providedIn:'root'},
  withState<NotificationState>(initialNotificationState),
  withProps(()=>({
    utilService:inject(UtilService),
    authService:inject(AuthService),
  })),
  withMethods((store, webSocketService = inject(NotificationService)) => ({
    connect(url: string, userId: string): void {
      patchState(store, { isConnected: true });
      webSocketService.connect(url, userId);
    },
    disconnect(): void {
      patchState(store, { isConnected: false });
      webSocketService.disconnect();
    },
    addNotification(notification: Notification): void {
      patchState(store, (state) => ({
        notifications: [...state.notifications, notification],
      }));
    },
  })),
  withHooks((state)=>{
    return {
      onInit(){
        const authService = state.authService;
        const userId = authService.loggedUserId();
        const isLoggedIn = authService.isLoggedIn();
        if(isLoggedIn && userId){
          state.connect('http://localhost:4010/ws',userId);
        }
      },
      onDestroy(){
        state.disconnect();
      }
    }
  })
);
