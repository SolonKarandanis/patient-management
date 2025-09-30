package com.pm.billingservice.repository;

import com.pm.billingservice.model.Billing;
import com.pm.billingservice.repository.rowmapper.BillingRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BillingRepositoryImpl implements BillingRepository {

    private final JdbcTemplate jdbcTemplate;

    private static String FIND_BY_ID = "SELECT * FROM billing WHERE id = ?";
    private static String DELETE_BY_ID = "DELETE FROM billing WHERE id = ?";

    public BillingRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Billing billing) {

    }

    @Override
    public Billing getById(int id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID,new BillingRowMapper(),id);
    }

    @Override
    public void update(Billing billing) {

    }

    @Override
    public void deleteById(int id) {
        int out = jdbcTemplate.update(DELETE_BY_ID, id);
    }

    @Override
    public List<Billing> getAll() {
        String FIND_ALL = "select * from billing";
        return jdbcTemplate.query(FIND_ALL,new BillingRowMapper());
    }
}
