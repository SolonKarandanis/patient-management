package com.pm.billingservice.repository;

import com.pm.billingservice.model.Billing;
import com.pm.billingservice.repository.rowmapper.BillingRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class BillingRepositoryImpl implements BillingRepository {

    private static final Logger log = LoggerFactory.getLogger(
            BillingRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private static String FIND_BY_ID = "SELECT * FROM billing WHERE id = ?";
    private static String DELETE_BY_ID = "DELETE FROM billing WHERE id = ?";

    public BillingRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public void save(Billing billing) {
        String INSERT_BILLING = "insert into billing(id,account_id,patient_id,account_name,account_email,account_status,created_date) " +
                "values(nextval('billing_seq'),?,?,?,?,?,?)";
        int result=jdbcTemplate.update(INSERT_BILLING,billing.getAccountId(),billing.getPatientId(),
                billing.getAccountName(),billing.getAccountEmail(),billing.getAccountStatus(),billing.getCreatedDate());
        log.info("billing saved {}", result);
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
