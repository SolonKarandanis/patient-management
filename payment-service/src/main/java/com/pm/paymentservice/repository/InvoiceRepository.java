package com.pm.paymentservice.repository;

import com.pm.paymentservice.model.Invoice;

import jakarta.persistence.Query;
import org.hibernate.jpa.HibernateHints;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class InvoiceRepository extends AbstractRepository<Invoice, Long> {

    public Invoice findByPublicId(UUID publicId){
        Query q = getEntityManager()
                .createQuery("SELECT inv FROM Invoice inv WHERE inv.publicId = :publicId")
                .setParameter("publicId", publicId)
                .setHint(HibernateHints.HINT_READ_ONLY, true);
        return (Invoice) q.getSingleResult();
    }
}
