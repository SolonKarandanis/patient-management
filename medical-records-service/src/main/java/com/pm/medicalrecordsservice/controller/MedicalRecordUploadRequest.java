package com.pm.medicalrecordsservice.controller;

import com.pm.medicalrecordsservice.model.GeoLocation;

public class MedicalRecordUploadRequest {
    private String patientId;
    private double latitude;
    private double longitude;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public GeoLocation toGeoLocation() {
        return new GeoLocation(this.latitude, this.longitude);
    }
}
