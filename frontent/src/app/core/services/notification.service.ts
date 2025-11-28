import {effect, inject, Injectable} from '@angular/core';
import {Client, StompSubscription} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {NotificationEvent} from '@models/notification.model';
import {UtilService} from '@core/services/util.service';
import {WS_BASE_URL} from '@core/token';
import {AuthService} from '@core/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly stompClient: Client;
  private isConnected = false;
  private userSubscription: StompSubscription | undefined;
  private subscriptions: StompSubscription[] = [];

  private readonly utilService = inject(UtilService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = inject(WS_BASE_URL);
  private isLoggedIn = this.authService.isLoggedIn;
  private userId = this.authService.loggedUserId;

  constructor() {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(this.baseUrl),
      reconnectDelay: 5000,
      heartbeatIncoming: 20000,
      heartbeatOutgoing: 20000,
    });
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

    this.stompClient.onConnect = this.onConnect;
    this.stompClient.onStompError = this.onError;
    this.stompClient.activate();
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

  private onError = () => {
    this.isConnected = false;
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
      if (this.stompClient.active) {
        this.stompClient.deactivate().then(() => {
          if (callback) {
            callback();
          }
        });
      } else if (callback) {
        callback();
      }
    }
    this.isConnected = false;
  }
}