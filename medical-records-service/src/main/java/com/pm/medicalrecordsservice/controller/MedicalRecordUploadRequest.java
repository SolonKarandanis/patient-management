package com.pm.medicalrecordsservice.controller;

import org.springframework.data.geo.Point;

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

    public Point toPoint() {
        return new Point(this.longitude, this.latitude);
    }
}
