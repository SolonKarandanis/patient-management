package com.pm.billingservice.repository;

import com.pm.billingservice.model.Billing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BillingRepositoryImpl implements BillingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BillingRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Billing billing) {

    }

    @Override
    public Billing getById(int id) {
        return null;
    }

    @Override
    public void update(Billing billing) {

    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public List<Billing> getAll() {
        return List.of();
    }
}
