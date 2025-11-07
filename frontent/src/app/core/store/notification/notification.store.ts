import {patchState, signalStore, withHooks, withMethods, withProps, withState} from '@ngrx/signals';
import { inject } from '@angular/core';
import {initialNotificationState, NotificationState} from '@core/store/notification/notification.state';
import {UtilService} from '@core/services/util.service';
import {Notification} from '@models/notification.model';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';


export const NotificationStore = signalStore(
  {providedIn:'root'},
  withState<NotificationState>(initialNotificationState),
  withProps(()=>({
    utilService:inject(UtilService),
  })),
  withMethods((store) => {
    return {
      connect(url: string, userId: string): void {
        patchState(store, { isConnected: true });

      },
      disconnect(): void {
        patchState(store, { isConnected: false });
        // if (this.stompClient !== null) {
        //   this.stompClient.disconnect();
        // }
        console.log("Disconnected");
      },
      addNotification(notification: Notification): void {
        patchState(store, (state) => ({
          notifications: [...state.notifications, notification],
        }));
      },
    }
  }),
  withHooks((state)=>{
    return {
      onInit(){
        // const authService = state.authService;
        // const userId = authService.loggedUserId();
        // const isLoggedIn = authService.isLoggedIn();
        // console.log("onInit")
        // if(isLoggedIn && userId){
        //   state.connect('http://localhost:4010/ws',userId);
        // }
      },
      onDestroy(){
        state.disconnect();
      }
    }
  })
);
