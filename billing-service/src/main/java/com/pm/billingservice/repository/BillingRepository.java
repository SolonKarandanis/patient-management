package com.pm.billingservice.repository;

import com.pm.billingservice.model.Billing;

import java.util.List;

public interface BillingRepository {

    void save(Billing billing);
    Billing getById(int id);
    Billing getByPatientId(String patientId);
    void update(Billing billing);
    void deleteById(int id);
    List<Billing> getAll();
    List<Billing> getByStatus(String status);
}
