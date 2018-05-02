package com.trivia.core.service;

import com.trivia.core.exception.EntityExistsException;
import com.trivia.core.exception.EntityNotFoundException;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.security.Cryptography;
import com.trivia.core.utility.SortOrder;
import com.trivia.persistence.entity.*;
import org.modelmapper.internal.util.TypeResolver;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Service<T> implements GenericDAO<T> {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;

    // TODO: Use annotations for some of this stuff.
    protected Integer PAGE_SIZE_DEFAULT;
    protected Integer PAGE_SIZE_MAX;
    protected SingularAttribute<T, ?> DEFAULT_SORT_COLUMN;
    protected List<SingularAttribute<T, ?>> SORTABLE_COLUMNS;
    protected List<SingularAttribute<T, ?>> SEARCHABLE_COLUMNS;

    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public Service() {
        entityClass = (Class<T>) TypeResolver.resolveArgument(this.getClass(), Service.class); // Magic.
    }

    public T findById(int id) {
        T entity = getById(id);
        if (entity == null) throw new EntityNotFoundException();
        return entity;
    }

    public T getById(int id) {
        T entity = em.find(entityClass, id);
        return entity;
    }

    public <V> T getByField(SingularAttribute<T, V> field, V value) {
        T entity;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        // TODO: Not sure if we want this to be optimised (it reuses the query if the param is the same type)
        // for all fields because of security concerns.
        ParameterExpression<V> parameter = builder.parameter(field.getJavaType(), field.getName());
        query.select(root).where(builder.equal(root.get(field), parameter));
        TypedQuery<T> typedQuery = em.createQuery(query).setParameter(field.getName(), value);

        /**
         * The JPA API really sucks regarding this. The NoResultException is actually the way getSingleResult()
         * is supposed to alert us no entity has been found.
         */
        try {
            entity = typedQuery.getSingleResult();
        }
        catch (NoResultException e) {
            entity = null;
        }

        return entity;
    }

    public <V> T findByField(SingularAttribute<T, V> field, V value) {
        T entity = getByField(field, value);
        if (entity == null) throw new EntityNotFoundException();
        return entity;
    }

    public void update(T updatedEntity) {
        //findById(); TODO: do we need this or is JPA going to call EntityNotFound()
        em.merge(updatedEntity);
        em.flush();
    }

    public void deleteById(int id) {
        T entity = findById(id);
        em.remove(entity);
        em.flush();
    }

    public void create(T entity) {
        em.persist(entity);
        em.flush();
    }

    public Long count() {
        Long count;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(entityClass);
        query.select(builder.count(root));
        TypedQuery<Long> typedQuery = em.createQuery(query);
        count = typedQuery.getSingleResult();

        return count;
    }

    public List<T> findAll(int pageCurrent, int pageSize, String sortColumn, SortOrder sortOrder, String searchString) {
        /* Init */
        List<T> entities;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        query.select(root);

        /* Sort */
        Path<?> path = getPath(sortColumn, root);
        if (path != null) {
            switch(sortOrder) {
                case ASCENDING:
                    query.orderBy(builder.asc(path));
                    break;
                case DESCENDING:
                    query.orderBy(builder.desc(path));
                    break;
                case UNSORTED:
                    query.orderBy(builder.desc(path));
                    break;
            }
        }

        /* Filter */
        query = setFilter(query, searchString, builder, root);

        /* Pagination */
        TypedQuery<T> typedQuery = em.createQuery(query);
        //TODO: TEMPORARY - read below
        lastCount = typedQuery.getResultList().size();
        pageSize = (pageSize >= 0 && pageSize <= PAGE_SIZE_MAX) ? pageSize : PAGE_SIZE_DEFAULT;
        typedQuery.setMaxResults(pageSize);
        pageCurrent = (pageCurrent >= 0) ? pageCurrent : 0;
        typedQuery.setFirstResult(pageCurrent * pageSize - pageSize);


        entities = typedQuery.getResultList();
        return entities;
    }

    // TODO: This method is kinda messy.
    @SuppressWarnings("unchecked")
    protected CriteriaQuery<T> setFilter(CriteriaQuery<T> query, String searchString, CriteriaBuilder builder, Root<T> root) {
        if (searchString == null || searchString.trim().length() < 1) {
            return query;
        }
        if (SEARCHABLE_COLUMNS.isEmpty()) throw new IllegalStateException("No searchable fields found, but search initiated.");

        Predicate filterCondition = builder.disjunction();
        for (SingularAttribute<T, ?> searchableField : SEARCHABLE_COLUMNS) {
            Expression<?> path = root.get(searchableField);
            Predicate predicate = null;

            boolean fieldMatched = false;
            Class<?> fieldType = searchableField.getJavaType();
            if (fieldType.equals(Integer.class)) {
                if (searchString.chars().allMatch(Character::isDigit)) {
                    predicate = builder.equal(path, Integer.parseInt(searchString));
                    fieldMatched = true;
                }
            }
            else if (fieldType.equals(Double.class)) {
                if (Pattern.matches("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$", searchString)) {
                    predicate = builder.equal(path, Double.valueOf(searchString));
                    fieldMatched = true;
                }
            }
            else if (fieldType.equals(String.class)) {
                predicate = builder.like((Expression<String>) path, "%" + searchString + "%");
                fieldMatched = true;
            }

            if (fieldMatched) {
                filterCondition = builder.or(filterCondition, predicate);
            }
        }
        query.where(filterCondition);
        return query;
    }

    protected Path<?> getPath(String column, Root<T> root) {
        if (column == null) {
            return root.get(DEFAULT_SORT_COLUMN);
        }
        if (SORTABLE_COLUMNS.isEmpty()) throw new IllegalStateException("No sortable fields found, but sort initiated.");

        for (SingularAttribute<T, ?> sortableField : SORTABLE_COLUMNS) {
            if (sortableField.getName().equals(column)) {
                return root.get(sortableField);
            }
        }

        throw new IllegalArgumentException(String.format("No column matching '%s' found.", column));
    }

    //TODO: TEMPORARY - This is crazy. Wrap the list inside a data structure (e.g. EntityPage -- has page out of x, randomized, sort field, sort order, count, etc.) to fix this.
    private int lastCount; public void setLastCount(int lastCount) {
        this.lastCount = lastCount;
    }public int getLastCount() {
        return lastCount;
    }
}
