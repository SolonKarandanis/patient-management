import {Notification} from '@models/notification.model';

export interface NotificationState{
  notifications: Notification[];
  isConnected: boolean;
}

export const initialNotificationState: NotificationState = {
  notifications: [],
  isConnected: false,
};
