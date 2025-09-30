package com.pm.billingservice.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Billing {

    private Integer id;
    private String patientId;
    private String accountId;
    private String accountName;
    private String accountEmail;
    private String accountStatus;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public Billing(String patientId,String accountName,String accountEmail){
        this.patientId = patientId;
        this.accountName = accountName;
        this.accountEmail = accountEmail;
        this.createdDate = LocalDateTime.now();
    }
}
