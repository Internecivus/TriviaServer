package com.trivia.core.service;

import com.trivia.core.exception.*;
import com.trivia.core.exception.EntityExistsException;
import com.trivia.core.exception.EntityNotFoundException;
import com.trivia.core.security.Cryptography;
import com.trivia.core.utility.Generator;
import com.trivia.core.utility.SortOrder;
import com.trivia.persistence.entity.*;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.List;

@Stateless
public class UserService {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Resource SessionContext sessionContext;
    private final static Integer PAGE_SIZE_DEFAULT = 20;
    private final static Integer PAGE_SIZE_MAX = 100;

    public UserEntity findById(int id) {
        UserEntity user = em.find(UserEntity.class, id);
        if (user == null) throw new EntityNotFoundException();
        return user;
    }

    public UserEntity findByName(String name) {
        UserEntity user = getByName(name);
        if (user == null) throw new EntityNotFoundException();
        return user;
    }

    private UserEntity getByName(String name) {
        UserEntity user;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = builder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        // TODO: Not sure if we want this to be optimised (it reuses the query if the param is the same type) because of security concerns.
        ParameterExpression<String> nameParameter = builder.parameter(String.class, UserEntity_.NAME);
        query.select(root).where(builder.equal(root.get(UserEntity_.NAME), nameParameter));
        TypedQuery<UserEntity> typedQuery = em.createQuery(query).setParameter(UserEntity_.NAME, name);

        /**
         * The JPA API really sucks regarding this. The NoResultException is actually the way getSingleResult()
         * is supposed to alert us no entity has been found.
         */
        try {
            user = typedQuery.getSingleResult();
        }
        catch (NoResultException e) {
            user = null;
        }

        return user;
    }

    private UserEntity promoteToProvider(UserEntity user) {
        if (!user.hasRole(Role.PROVIDER)) {    // Double check.
            throw new IllegalStateException();
        }
        String providerKey = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);
        String providerSecret = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);

        user.setProviderKey(Cryptography.hashMessage(providerKey));
        user.setProviderSecret(Cryptography.hashMessage(providerSecret));

        return user;
    }

    public void validateProvider(String providerId, String providerSecret) {
        //TODO: just as validateCrendetial just for providers
    }

    public void update(UserEntity updatedUser) {
        UserEntity user = findById(updatedUser.getId());
        em.persist(updatedUser);
        em.flush();
    }

    public void deleteById(int id) {
        if (!sessionContext.isCallerInRole(Role.ADMIN.toString())) throw new NotAuthorizedException();
        UserEntity user = findById(id);
        em.remove(user);
        em.flush();
    }

    public UserEntity validateCredential(String name, String password) {
        UserEntity user = findByName(name);
        if (Cryptography.validateMessage(password, user.getPassword())) {
            return user;
        }
        else {
            throw new InvalidCredentialException();
        }
    }

    public void create(UserEntity newUser) {
        if (getByName(newUser.getName()) != null) throw new EntityExistsException();
        if (newUser.hasRole(Role.PROVIDER)) {
            newUser = promoteToProvider(newUser);
        }

        newUser.setDateCreated(new Timestamp(System.currentTimeMillis()));
        newUser.setPassword(Cryptography.hashMessage(newUser.getPassword()));

        em.persist(newUser);
        em.flush();
    }

    public List<UserEntity> findAll(int pageCurrent, int pageSize, String sortField, SortOrder sortOrder, String searchString) {
        List<UserEntity> questions;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = builder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);
        query.select(root);
        Path<?> path = getPath(sortField, root);


        if (searchString != null && searchString.trim().length() > 0) {
            Predicate filterCondition = builder.disjunction();
            if (searchString.chars().allMatch(Character::isDigit)) {
                filterCondition = builder.or(filterCondition, builder.equal(root.get(UserEntity_.id), Integer.parseInt(searchString)));
            }
            filterCondition = builder.or(filterCondition, builder.like(root.get(UserEntity_.name), "%" + searchString + "%"));
            query.where(filterCondition);
        }

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

        TypedQuery<UserEntity> typedQuery = em.createQuery(query);

        //TODO: TEMPORARY - read below
        lastCount = typedQuery.getResultList().size();

        pageSize = (pageSize >= 0 && pageSize <= PAGE_SIZE_MAX) ? pageSize : PAGE_SIZE_DEFAULT;
        typedQuery.setMaxResults(pageSize);
        pageCurrent = (pageCurrent >= 0) ? pageCurrent : 0;
        typedQuery.setFirstResult(pageCurrent * pageSize - pageSize);

        questions = typedQuery.getResultList();

        return questions;
    }

    private Path<?> getPath(String field, Root<UserEntity> root) {
        Path<?> path;
        if (field == null) {
            path = root.get(UserEntity_.dateCreated);
        }
        else {
            switch (field) {
                case "id":
                    path = root.get(UserEntity_.id);
                    break;
                case "name":
                    path = root.get(UserEntity_.NAME);
                    break;
                case "dateCreated":
                    path = root.get(UserEntity_.dateCreated);
                    break;
                default:
                    path = root.get(UserEntity_.dateCreated);
                    break;
            }
        }
        return path;
    }

    // TODO
    private int lastCount; public void setLastCount(int lastCount) {
        this.lastCount = lastCount;
    }public int getLastCount() {
        return 1;
    }
}