package com.pm.creaditcardservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.SynchronizationType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public abstract class AbstractRepository<T, ID extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected static final int BATCH_SIZE = 1000;
    private static final String SELECT_FROM="select o from ";
    private static final String FIELD_VALUE="fieldValue";

    private final Class<T> entityClass;

    @jakarta.persistence.PersistenceContext(type = PersistenceContextType.TRANSACTION,
            synchronization = SynchronizationType.SYNCHRONIZED)
    private EntityManager em;

    @SuppressWarnings("unchecked")
    protected AbstractRepository() {
        this.entityClass = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    public T create(T entity) {

        // checking for constraint violations
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<T> cv : constraintViolations) {
                log.error("{}.{} {}", cv.getRootBeanClass().getName(), cv.getPropertyPath(), cv.getMessage());
            }
        } else {
            getEntityManager().persist(entity);
            return entity;
        }
        return null;
    }

    public T edit(T entity) {
        // checking for constraint violations
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<T> cv : constraintViolations) {
                log.error("{}.{} {}", cv.getRootBeanClass().getName(), cv.getPropertyPath(), cv.getMessage());
            }
        } else {
            return getEntityManager().merge(entity);
        }

        return null;
    }

    public T find(ID id) {
        return (T) getEntityManager().find(getEntityClass(), id);
    }

    public Optional<T> findOpt(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return Optional.ofNullable((T) getEntityManager().find(getEntityClass(), id));
    }

    public Optional<T> findReferenceOpt(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return Optional.ofNullable((T) getEntityManager().getReference(getEntityClass(), id));
    }

    public void delete(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public void delete(ID id) {
        Object ref = getEntityManager().getReference(getEntityClass(), id);
        getEntityManager().remove(ref);
    }

    public List<T> fetchIn(List<ID> ids) {
        MultiIdentifierLoadAccess<T> multi = getSession().byMultipleIds(getEntityClass());
        return multi.multiLoad(ids);
    }

    public Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }

    public boolean isManaged(Class<T> entityClass) {
        return getEntityManager().contains(entityClass);
    }

    public void clearContext() {
        getEntityManager().clear();
    }

    public void flushContext() {
        getEntityManager().flush();
    }

    public EntityEntry getEntityEntry(Class<T> entityClass) {
        return getPersistenceContext().getEntry(entityClass);
    }

    public Object[] getLoadedState(Class<T> entityClass) {
        return getEntityEntry(entityClass).getLoadedState();
    }

    // inspect the Persistence Context,with  the following helper method
    private PersistenceContext getPersistenceContext() {
        SharedSessionContractImplementor sharedSession = getEntityManager().unwrap(
                SharedSessionContractImplementor.class
        );
        return sharedSession.getPersistenceContext();
    }

    public int countAll() {
        CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(getEntityClass());
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        TypedQuery<Object> q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public List<T> bulkCreate(List<T> entities) {
        final List<T> savedEntities = new ArrayList<>(entities.size());
        int i = 0;
        for (T t : entities) {
            getEntityManager().persist(t);
            savedEntities.add(t);
            i++;
            if (i % BATCH_SIZE == 0) {
                // Flush a batch of inserts and release memory.
                getEntityManager().flush();
                getEntityManager().clear();
            }
        }
        return savedEntities;
    }


    public List<T> bulkUpdate(List<T> entities) {
        final List<T> updateEntities = new ArrayList<>(entities.size());
        int i = 0;
        for (T t : entities) {
            getEntityManager().merge(t);
            updateEntities.add(t);
            i++;
            if (i % BATCH_SIZE == 0) {
                // Flush a batch of inserts and release memory.
                getEntityManager().flush();
                getEntityManager().clear();
            }
        }
        return updateEntities;
    }
}
