import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private stompClient: any;

  constructor() { }

  public connect(url: string, userId: string): void {
    console.log(`Attempting to connect to WebSocket at ${url} for user ${userId}`);
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);

    const onConnect = (frame: any) => {
      console.log('STOMP Connected: ' + frame);

      // Subscription for actual notifications
      this.stompClient.subscribe(`/topic/notifications/${userId}`, (message: any) => {
        console.log('Received NOTIFICATION:', message.body);
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
