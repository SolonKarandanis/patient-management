package com.pm.billingservice.repository.rowmapper;

import com.pm.billingservice.model.Billing;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BillingRowMapper implements RowMapper<Billing> {

    @Override
    public Billing mapRow(ResultSet rs, int rowNum) throws SQLException {
        Billing billing = new Billing();
        billing.setId(rs.getInt("id"));
        billing.setPatientId(rs.getString("patient_id"));
        billing.setAccountId(UUID.fromString(rs.getString("account_id")));
        billing.setAccountName(rs.getString("account_name"));
        billing.setAccountEmail(rs.getString("account_email"));
        billing.setAccountStatus(rs.getString("account_status"));
        billing.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
        billing.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
        return billing;
    }
}
