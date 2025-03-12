package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PatientRequestDTO {
    @NotBlank(message = "{error.generic.prefix} {prompt.patient.name} {error.generic.required}")
    @Size(max = 100, message = "{prompt.patient.name} {error.generic.oversize.MAX}")
    private String name;

    @NotBlank(message = "{error.generic.prefix} {prompt.patient.email} {error.generic.required}")
    @Email(message = "{prompt.patient.email} {error.generic.valid}")
    private String email;

    @NotBlank(message = "{error.generic.prefix} {prompt.patient.address} {error.generic.required}")
    private String address;

    @NotBlank(message = "{error.generic.prefix} {prompt.patient.dateOfBirth} {error.generic.required}")
    private String dateOfBirth;

    @NotBlank(groups = CreatePatientValidationGroup.class, message =
            "{error.generic.prefix} {prompt.patient.registeredDate} {error.generic.required}")
    private String registeredDate;

    public @NotBlank(message = "{error.generic.prefix} {prompt.patient.name} {error.generic.required}") @Size(max = 100, message = "{prompt.patient.name} {error.generic.oversize.MAX}") String getName() {
        return name;
    }

    public void setName(
            @NotBlank(message = "{error.generic.prefix} {prompt.patient.name} {error.generic.required}") @Size(max = 100, message = "{prompt.patient.name} {error.generic.oversize.MAX}") String name) {
        this.name = name;
    }

    public @NotBlank(message = "{error.generic.prefix} {prompt.patient.email} {error.generic.required}") @Email(message = "{prompt.patient.email} {error.generic.valid}") String getEmail() {
        return email;
    }

    public void setEmail(
            @NotBlank(message = "{error.generic.prefix} {prompt.patient.email} {error.generic.required}") @Email(message = "{prompt.patient.email} {error.generic.valid}") String email) {
        this.email = email;
    }

    public @NotBlank(message = "{error.generic.prefix} {prompt.patient.address} {error.generic.required}") String getAddress() {
        return address;
    }

    public void setAddress(
            @NotBlank(message = "{error.generic.prefix} {prompt.patient.address} {error.generic.required}") String address) {
        this.address = address;
    }

    public @NotBlank(message = "{error.generic.prefix} {prompt.patient.dateOfBirth} {error.generic.required}") String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(
            @NotBlank(message = "{error.generic.prefix} {prompt.patient.dateOfBirth} {error.generic.required}") String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

}
