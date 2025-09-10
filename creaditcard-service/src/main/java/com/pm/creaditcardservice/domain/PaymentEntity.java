package com.pm.creaditcardservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="payments")
public class PaymentEntity {
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

    @Enumerated(EnumType.STRING)
    private PaymentState state;

    @Column(name = "amount")
    private BigDecimal amount;

    @NaturalId
    @Column(name = "public_id",nullable = false, updatable = false, unique = true)
    private UUID publicId;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;
}
