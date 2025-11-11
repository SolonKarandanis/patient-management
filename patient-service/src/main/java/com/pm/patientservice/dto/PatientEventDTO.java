package com.pm.patientservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientEventDTO {

    private String publicId;
    private String patientId;
    private String name;
    private String email;
}
