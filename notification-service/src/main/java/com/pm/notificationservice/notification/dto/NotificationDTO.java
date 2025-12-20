package com.pm.notificationservice.notification.dto;

public record NotificationDTO(String title, String message, String eventType) {

    public NotificationDTO(String eventType){
        this(null,null,eventType);
    }
}
