package com.pm.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "invoiceGenerator"
    )
    @SequenceGenerator(
            name = "invoiceGenerator",
            sequenceName = "invoice_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Basic(optional = false)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "patient_public_id", nullable = false)
    private UUID patientPublicId;

    @Column(name = "total_amount", precision = 10, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
}
