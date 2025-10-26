package com.pm.paymentservice.repository;

import com.pm.paymentservice.model.PaymentEntity;
import com.pm.paymentservice.model.PaymentState;
import jakarta.persistence.Query;
import org.hibernate.jpa.HibernateHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class PaymentRepository extends AbstractRepository<PaymentEntity,Integer> {

    public PaymentEntity findByPaymentPublicId(UUID paymentPublicId) {
        Query q = getEntityManager().createQuery(PaymentEntity.FIND_BY_PUBLIC_ID)
                .setParameter("publicId", paymentPublicId)
                .setHint(HibernateHints.HINT_READ_ONLY, true);
        return (PaymentEntity) q.getSingleResult();
    }

    public List<PaymentEntity> findByPatientPublicId(UUID patientPublicId) {
        Query q = getEntityManager().createQuery(PaymentEntity.FIND_BY_PATIENT_PUBLIC_ID)
                .setParameter("patientPublicId", patientPublicId)
                .setHint(HibernateHints.HINT_READ_ONLY, true);
        return (List<PaymentEntity>) q.getResultList();
    }

    public List<PaymentEntity> findByPatientId(Integer patientId) {
        Query q = getEntityManager().createQuery(PaymentEntity.FIND_BY_PATIENT_ID)
                .setParameter("patientId", patientId)
                .setHint(HibernateHints.HINT_READ_ONLY, true);
        return (List<PaymentEntity>) q.getResultList();
    }

    public List<PaymentEntity> findByState(PaymentState state) {
        Query q = getEntityManager().createQuery(PaymentEntity.FIND_BY_PATIENT_STATE)
                .setParameter("state", state)
                .setHint(HibernateHints.HINT_READ_ONLY, true);
        return (List<PaymentEntity>) q.getResultList();
    }

    public List<PaymentEntity> findNewPayments(){
        return findByState(PaymentState.NEW);
    }

    public List<PaymentEntity> findPreAuthedPayments(){
        return findByState(PaymentState.PRE_AUTH);
    }

    public List<PaymentEntity> findPreAuthErroredPayments(){
        return findByState(PaymentState.PRE_AUTH_ERROR);
    }

    public List<PaymentEntity> findAuthedPayments(){
        return findByState(PaymentState.AUTH);
    }

    public List<PaymentEntity> findAuthErroredPayments(){
        return findByState(PaymentState.AUTH_ERROR);
    }
}
