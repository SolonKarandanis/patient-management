import {effect, inject, Injectable} from '@angular/core';
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
  private subscriptions: Stomp.Subscription[] = [];
  private userSubscription: Stomp.Subscription | undefined;

  private readonly utilService = inject(UtilService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = inject(WS_BASE_URL);
  private isLoggedIn = this.authService.isLoggedIn;
  private userId = this.authService.loggedUserId;

  constructor() {
    const socket = new SockJS(this.baseUrl);
    this.stompClient = Stomp.over(socket);
    this.connect();

    effect(() => {
      const userId = this.userId();
      if (this.isConnected) {
        if (userId && !this.userSubscription) {
          this.userSubscription = this.stompClient.subscribe(`/topic/notifications/${userId}`, (message: any) => {
            const notification = JSON.parse(message.body) as NotificationEvent;
            this.utilService.handleNotification(notification);
          });
        } else if (!userId && this.userSubscription) {
          this.userSubscription.unsubscribe();
          this.userSubscription = undefined;
        }
      }
    });
  }

  public connect(): void {
    if (this.isConnected) {
      return;
    }

    this.stompClient.connect({}, this.onConnect, this.onError);
    this.stompClient.heartbeat.outgoing = 20000;
    this.stompClient.heartbeat.incoming = 20000;
  }

  private onConnect = () => {
    this.isConnected = true;
    this.subscribeToTopics();
    const userId = this.userId();
    if (userId && !this.userSubscription) {
      this.userSubscription = this.stompClient.subscribe(`/topic/notifications/${userId}`, (message: any) => {
        const notification = JSON.parse(message.body) as NotificationEvent;
        this.utilService.handleNotification(notification);
      });
    }
    return;
  };

  private onError = (error: any) => {
    this.isConnected = false;
    setTimeout(() => {
      this.connect();
    }, 5000);
  };

  private subscribeToTopics(): void {
    this.subscriptions.push(this.stompClient.subscribe(`/topic/notifications`, (message: any) => {
      let notification = JSON.parse(message.body);
      this.utilService.handleNotification(notification as NotificationEvent);
    }));
  }

  public disconnect(callback?: () => void): void {
    if (this.stompClient !== null) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
      if (this.userSubscription) {
        this.userSubscription.unsubscribe();
        this.userSubscription = undefined;
      }
      this.subscriptions = [];
      if (this.stompClient.connected) {
        this.stompClient.disconnect(callback);
      } else if (callback) {
        callback();
      }
    }
    this.isConnected = false;
  }
}
