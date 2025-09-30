package com.pm.billingservice.model;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Billing {
    @Id
    private Integer id;
    private String patientId;
    private UUID accountId;
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
