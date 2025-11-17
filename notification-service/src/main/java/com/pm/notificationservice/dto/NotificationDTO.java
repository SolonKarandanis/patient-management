package com.pm.notificationservice.dto;

public record NotificationDTO(String title, String message, String eventType) {

    public NotificationDTO(String eventType){
        this(null,null,eventType);
    }
}
