import {inject, Injectable} from '@angular/core';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {NotificationEvent} from '@models/notification.model';
import {UtilService} from '@core/services/util.service';
import {WS_BASE_URL} from '@core/token';
import {AuthService} from '@core/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private stompClient: any;
  private isConnected = false;

  private readonly utilService = inject(UtilService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = inject(WS_BASE_URL);

  constructor() {
    const socket = new SockJS(this.baseUrl);
    this.stompClient = Stomp.over(socket);
  }

  public connect(): void {
    if (this.isConnected) {
      return;
    }

    const onConnect = () => {
      // Subscription for actual notifications
      const isLoggedIn = this.authService.isLoggedIn();
      const userId = this.authService.loggedUserId();
      if(isLoggedIn && userId && !this.isConnected){
        this.stompClient.subscribe(`/topic/notifications/${userId}`, (message: any) => {
          const notification = JSON.parse(message.body) as NotificationEvent;
          this.utilService.showMessage("warn",notification.message,notification.title)
        });
      }


      // Subscription for echo test
      // this.stompClient.subscribe('/topic/greetings', (greeting: any) => {
      //   console.log('Received GREETING:', greeting.body);
      // });

      // Send test message to trigger echo
      // console.log('Sending hello message to /app/hello...');
      // this.stompClient.send('/app/hello', {}, `Test message from user ${userId}`);
    };

    const onError = (error: any) => {
      console.error('STOMP Error: ', error);
    };
    this.stompClient.connect({}, onConnect, onError);
  }

  public disconnect(callback?: () => void): void {
    if (this.stompClient !== null) {
      this.stompClient.disconnect(callback);
    }
    this.isConnected = false;
  }
}
