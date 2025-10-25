package com.pm.creaditcardservice.repository;

import com.pm.creaditcardservice.repository.utils.FacadeUtils;
import com.pm.creaditcardservice.repository.utils.StatementParameter;
import com.pm.creaditcardservice.utils.StringUtils;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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

    public T findById(ID id) {
        return (T) getEntityManager().find(getEntityClass(), id);
    }

    public Optional<T> findByIdOpt(ID id) {
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

    public List<T> findByField(String fieldName, String fieldValue, Class<?> fieldType) {
        String fieldStr = fieldName;
        if (String.class.equals(fieldType)) {
            fieldStr = "lower(o." + fieldName + ")";
        }
        String sQuery = SELECT_FROM + getEntityClass().getSimpleName()
                + " o where " + fieldStr + " = :"+FIELD_VALUE;
        Query hsql = getEntityManager().createQuery(sQuery);
        if (Long.class.equals(fieldType)) {
            hsql.setParameter(FIELD_VALUE, Long.valueOf(fieldValue));
        } else if (Integer.class.equals(fieldType)) {
            hsql.setParameter(FIELD_VALUE, Integer.valueOf(fieldValue));
        } else {
            hsql.setParameter(FIELD_VALUE, fieldValue != null ? fieldValue.trim().toLowerCase()
                    : null);
        }
        List<T> list = hsql.getResultList();
        return list;
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

    public List<T> findAll() {
        CriteriaQuery<Object>  cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(getEntityClass()));
        return (List<T>) getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findAll(T entity, String sortField, boolean asc) throws Exception {
        return findAllRange(entity, -1, -1, sortField, asc);
    }

    public List<T> findAllRange(T entity, int first, int pageSize, String sortField, boolean asc) throws Exception {
        // builder generates the Criteria Query as well as all the expressions
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        // The query declares what type of result it will produce
        CriteriaQuery<T> q = cb.createQuery(getEntityClass());
        // Which type will be searched
        Root<T> from = q.from(getEntityClass());
        // of course, the projection term must match the result type declared earlier
        q.select(from);
        // Builds the predicates conditionally for the filled-in input fields
        ArrayList<StatementParameter> params = generateParams(entity);
        List<Predicate> predicates = generatePredicates(cb, from, params);
        // Sets the evaluation criteria
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!isReturnAllAllowed()) {
            return new ArrayList<>();
        }
        if (sortField != null) {
            if (asc) {
                q.orderBy(cb.asc(getExpression(from, sortField)));
            } else {
                q.orderBy(cb.desc(getExpression(from, sortField)));
            }
        }
        Query query = getEntityManager().createQuery(q);
        if ((pageSize > -1) && (first > -1)) {
            query.setMaxResults(pageSize);
            query.setFirstResult(first);
        }
        return query.getResultList();
    }

    public ArrayList<StatementParameter> generateParams(T entity) throws Exception {
        ArrayList<StatementParameter> params = FacadeUtils.getDirty(entity);
        return params;
    }

    public List<Predicate> generatePredicates(CriteriaBuilder cb, Root<T> from, List<StatementParameter> params) throws Exception {
        List<Predicate> predicates = new ArrayList<>();
        for (StatementParameter param : params) {
            Predicate predicate;
            if (param.getType().equals(String.class)) {
                predicate=setPredicateForString(cb,from,param);
            } else if (param.getType().equals(Date.class)) {
                predicate=setPredicateForDate(cb,from,param);
            } else if (param.getType().equals(Integer.class)) {
                predicate=setPredicateForInteger(cb,from,param);
            } else if (param.getType().equals(BigInteger.class)) {
                predicate=setPredicateForBigInteger(cb,from,param);
            } else if (param.getType().equals(BigDecimal.class)) {
                predicate=setPredicateForBigDecimal(cb,from,param);
            } else if (param.getType().equals(Long.class)) {
                predicate=setPredicateForLong(cb,from,param);
            } else if (param.getType().equals(Short.class)) {
                predicate=setPredicateForShort(cb,from,param);
            } else {
                predicate = cb.equal(getExpression(from, param.getColumn()), param.getValue());
            }
            predicates.add(predicate);
        }
        return predicates;
    }

    private Predicate setPredicateForString(CriteriaBuilder cb,Root<T> from,StatementParameter param) {
        Expression<String> expr = getExpression(from, param.getColumn());
        Predicate predicate;
        if ("<".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThan(expr, (String) param.getValue());
        } else if ("<=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThanOrEqualTo(expr, (String) param.getValue());
        } else if (">".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThan(expr, (String) param.getValue());
        } else if (">=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThanOrEqualTo(expr, (String) param.getValue());
        } else if ("LIKE".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.like(expr, "%" + param.getValue() + "%");
        } else {
            predicate = cb.equal(expr, param.getValue());
        }
        return predicate;
    }

    private Predicate setPredicateForDate(CriteriaBuilder cb,Root<T> from,StatementParameter param) {
        Predicate predicate;
        Expression<Date> expr = getExpression(from, param.getColumn());
        if ("<".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThan(expr, (Date) param.getValue());
        } else if ("<=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThanOrEqualTo(expr, (Date) param.getValue());
        } else if (">".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThan(expr, (Date) param.getValue());
        } else if (">=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThanOrEqualTo(expr, (Date) param.getValue());
        } else {
            predicate = cb.equal(expr, param.getValue());
        }
        return predicate;
    }

    private Predicate setPredicateForInteger(CriteriaBuilder cb,Root<T> from,StatementParameter param) {
        Predicate predicate;
        Expression<Integer> expr = getExpression(from, param.getColumn());
        if ("<".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThan(expr, (Integer) param.getValue());
        } else if ("<=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThanOrEqualTo(expr, (Integer) param.getValue());
        } else if (">".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThan(expr, (Integer) param.getValue());
        } else if (">=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThanOrEqualTo(expr, (Integer) param.getValue());
        } else {
            predicate = cb.equal(expr, param.getValue());
        }
        return predicate;
    }

    private Predicate setPredicateForLong(CriteriaBuilder cb,Root<T> from,StatementParameter param) {
        Predicate predicate;
        Expression<Long> expr = getExpression(from, param.getColumn());
        if ("<".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThan(expr, (Long) param.getValue());
        } else if ("<=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThanOrEqualTo(expr, (Long) param.getValue());
        } else if (">".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThan(expr, (Long) param.getValue());
        } else if (">=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThanOrEqualTo(expr, (Long) param.getValue());
        } else {
            predicate = cb.equal(expr, param.getValue());
        }
        return predicate;
    }

    private Predicate setPredicateForShort(CriteriaBuilder cb,Root<T> from,StatementParameter param) {
        Predicate predicate;
        Expression<Short> expr = getExpression(from, param.getColumn());
        if ("<".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThan(expr, (Short) param.getValue());
        } else if ("<=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThanOrEqualTo(expr, (Short) param.getValue());
        } else if (">".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThan(expr, (Short) param.getValue());
        } else if (">=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThanOrEqualTo(expr, (Short) param.getValue());
        } else {
            predicate = cb.equal(expr, param.getValue());
        }
        return predicate;
    }

    private Predicate setPredicateForBigInteger(CriteriaBuilder cb,Root<T> from,StatementParameter param) {
        Predicate predicate;
        Expression<BigInteger> expr = getExpression(from, param.getColumn());
        if ("<".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThan(expr, (BigInteger) param.getValue());
        } else if ("<=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThanOrEqualTo(expr, (BigInteger) param.getValue());
        } else if (">".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThan(expr, (BigInteger) param.getValue());
        } else if (">=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThanOrEqualTo(expr, (BigInteger) param.getValue());
        } else {
            predicate = cb.equal(expr, param.getValue());
        }
        return predicate;
    }

    private Predicate setPredicateForBigDecimal(CriteriaBuilder cb,Root<T> from,StatementParameter param) {
        Predicate predicate;
        Expression<BigDecimal> expr = getExpression(from, param.getColumn());
        if ("<".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThan(expr, (BigDecimal) param.getValue());
        } else if ("<=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.lessThanOrEqualTo(expr, (BigDecimal) param.getValue());
        } else if (">".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThan(expr, (BigDecimal) param.getValue());
        } else if (">=".equalsIgnoreCase(param.getOperator())) {
            predicate = cb.greaterThanOrEqualTo(expr, (BigDecimal) param.getValue());
        } else {
            predicate = cb.equal(expr, param.getValue());
        }
        return predicate;
    }

    public Long count(T entity) throws Exception {
        // builder generates the Criteria Query as well as all the expressions
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        // The query declares what type of result it will produce
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        // Which type will be searched
        Root<T> from = q.from(getEntityClass());
        // of course, the projection term must match the result type declared earlier
        q.select(cb.count(from));
        // Builds the predicates conditionally for the filled-in input fields
        ArrayList<StatementParameter> params = generateParams(entity);
        List<Predicate> predicates = generatePredicates(cb, from, params);

        // Sets the evaluation criteria
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(new Predicate[0]));
        } else if (!isReturnAllAllowed()) {
            return 0L;
        }
        return getEntityManager().createQuery(q).getSingleResult();
    }

    public boolean isReturnAllAllowed() {
        return true;
    }

    public Expression getExpression(Root<T> from, String propertyname) {
        Expression retValue;
        if (!propertyname.contains(".")) {
            retValue = from.get(propertyname);
        } else {
            String[] fields = StringUtils.split(propertyname, ".");
            assert fields != null;
            Path<T> path = from.get(fields[0]);
            for (int i = 1; i < fields.length; i++) {
                path = path.get(fields[i]);
            }
            retValue = path;
        }
        return retValue;
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
