import {inject, Injectable} from '@angular/core';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {Notification} from '@models/notification.model';
import {UtilService} from '@core/services/util.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private stompClient: any;

  private utilService = inject(UtilService);


  public connect(url: string, userId: string): void {
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);

    const onConnect = (frame: any) => {
      // Subscription for actual notifications
      this.stompClient.subscribe(`/topic/notifications/${userId}`, (message: any) => {
        const notification = JSON.parse(message.body) as Notification;
        this.utilService.showMessage("warn",notification.message,notification.title)
      });

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

  public disconnect(): void {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log("Disconnected");
  }
}
