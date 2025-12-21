package com.pm.notificationservice.email.dto;

import lombok.Getter;

import lombok.NoArgsConstructor;

import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EmailDTO {

    private Integer id;
    private String dateCreated;
    private String dateSent;
    private String headerBcc;
    private String headerCc;
    private String headerFrom;
    private String headerReplyTo;
    private String headerSubject;
    private String headerTo;
    private String status;
    private Integer emailTypeId;
    private String emailTypeKey;
    private String details1;
    private String messageBody;
}
