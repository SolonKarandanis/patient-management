package com.pm.billingservice.repository;

import com.pm.billingservice.model.Billing;
import com.pm.billingservice.repository.rowmapper.BillingRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Repository
public class BillingRepositoryImpl implements BillingRepository {

    private static final Logger log = LoggerFactory.getLogger(
            BillingRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final TransactionTemplate txTemplate;
    private final JdbcClient jdbcClient;

    public BillingRepositoryImpl(
            JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            TransactionTemplate txTemplate,
            JdbcClient jdbcClient
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.txTemplate = txTemplate;
        this.jdbcClient = jdbcClient;
    }

    @Transactional
    @Override
    public void save(Billing billing) {
        String INSERT_BILLING = "insert into billing(id,account_id,patient_id,account_name,account_email,account_status,created_date) " +
                "values(nextval('billing_seq'),?,?,?,?,?,?)";
        int result=jdbcTemplate.update(INSERT_BILLING,billing.getAccountId(),billing.getPatientId(),
                billing.getAccountName(),billing.getAccountEmail(),billing.getAccountStatus(),billing.getCreatedDate());
        log.info("billing saved {}", Optional.of(result));
    }

    @Override
    public Billing getById(int id) {
        String FIND_BY_ID = "SELECT * FROM billing WHERE id = ?";
        return jdbcTemplate.queryForObject(FIND_BY_ID,new BillingRowMapper(),id);
    }

    @Override
    public Billing getByPatientId(String patientId) {
        String FIND_BY_PATIENT_ID = "SELECT * FROM billing WHERE patient_id = :patientId";
        return jdbcClient.sql(FIND_BY_PATIENT_ID)
                .param("patientId",patientId)
                .query(new BillingRowMapper())
                .single();
    }

    @Override
    public void update(Billing billing) {

    }

    @Override
    public void deleteById(int id) {
        String DELETE_BY_ID = "DELETE FROM billing WHERE id = ?";
        int out = jdbcTemplate.update(DELETE_BY_ID, id);
    }

    @Override
    public List<Billing> getAll() {
        String FIND_ALL = "select * from billing";
        return jdbcTemplate.query(FIND_ALL,new BillingRowMapper());
    }

    @Override
    public List<Billing> getByStatus(String status) {
       String  FIND_BY_STATUS = "select * from billing where account_status = :accountStatus";
       return jdbcClient.sql(FIND_BY_STATUS)
               .param("accountStatus",status)
               .query(new BillingRowMapper())
               .list();
    }
}
