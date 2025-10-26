package com.pm.paymentservice.model;

import com.pm.paymentservice.dto.PaymentStats;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = PaymentEntity.FIND_BY_PUBLIC_ID,
        query = "SELECT pe FROM PaymentEntity pe "
                + "WHERE pe.publicId= :publicId ")
@NamedQuery(name = PaymentEntity.FIND_BY_PATIENT_PUBLIC_ID,
        query = "SELECT pe FROM PaymentEntity pe "
                + "WHERE pe.patientPublicId= :patientPublicId ")
@NamedQuery(name = PaymentEntity.FIND_BY_PATIENT_STATE,
        query = "SELECT pe FROM PaymentEntity pe "
                + "WHERE pe.state= :state ")
@NamedStoredProcedureQuery(
        name = PaymentEntity.GET_PAYMENT_STATS,
        procedureName = PaymentEntity.DB_FN_GET_PAYMENT_STATS,
        resultClasses = PaymentStats.class,
        parameters={
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_patient_id", type = BigInteger.class),
        }
)
@Entity
@Table(name="payments")
public class PaymentEntity {

    public static final String FIND_BY_PUBLIC_ID= "PaymentEntity.findByPublicId";
    public static final String FIND_BY_PATIENT_PUBLIC_ID= "PaymentEntity.findByPatientPublicId";
    public static final String FIND_BY_PATIENT_STATE= "PaymentEntity.findByState";
    public static final String GET_PAYMENT_STATS = "PaymentEntity.getPaymentStats";

    public static final String DB_FN_GET_PAYMENT_STATS = "get_payment_stats";

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "paymentGenerator"
    )
    @SequenceGenerator(
            name = "paymentGenerator",
            sequenceName = "payment_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @NaturalId
    @Column(name = "public_id",nullable = false, updatable = false, unique = true)
    private UUID publicId;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "patient_public_id",nullable = false, updatable = false, unique = true)
    private UUID patientPublicId;

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;
}
