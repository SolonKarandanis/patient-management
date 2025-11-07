import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { NotificationStore } from '../store/notification/notification.store';
import { inject } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private stompClient: any;
  private readonly store = inject(NotificationStore);

  constructor() { }

  public connect(url: string, userId: string): void {
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, (frame: any) => {
      console.log('Connected: ' + frame);
      this.stompClient.subscribe(`/user/${userId}/notifications`, (message: any) => {
        this.store.addNotification(message.body);
      });
    });
  }

  public disconnect(): void {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log("Disconnected");
  }
}
