package com.pm.billingservice.repository;

import com.pm.billingservice.model.Billing;

import java.util.List;

public interface BillingRepository {

    public void save(Billing billing);
    public Billing getById(int id);
    public void update(Billing billing);
    public void deleteById(int id);
    public List<Billing> getAll();
}
