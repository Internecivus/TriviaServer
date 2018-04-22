package com.trivia.core.service;

import com.trivia.core.exception.EntityException;
import com.trivia.core.exception.EntityExistsException;
import com.trivia.core.security.CryptoManager;
import com.trivia.persistence.entity.QuestionEntity;
import com.trivia.persistence.entity.UserEntity;
import com.trivia.persistence.entity.UserEntity_;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Stateless
public class UserBean {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;

    public UserEntity findById(int id) {
        return em.find(UserEntity.class, id);
    }

    /**
     * The JPA API really sucks regarding this. The NoResultException is actually the way getSingleResult()
     * is supposed to alert us no entity has been found. Since this is a bad way to utilize exceptions, we could manage
     * this in three ways:
     * EITHER Use getResultList() and check for the size of the array, which is not a very OOP way.
     * OR doing the try-catch block in the calling method, which is breaking the Single Responsibility Principle.
     * OR doing the try-catch block in the called method, which is what we ended up doing.
     */
    public UserEntity findByName(String name) {
        UserEntity user;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = builder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        // Not sure if we want this to be optimised (it reuses the query if the param is the same type).
        ParameterExpression<String> nameParameter = builder.parameter(String.class, "name");
        query.select(root).where(builder.equal(root.get(UserEntity_.NAME), nameParameter));

        TypedQuery<UserEntity> typedQuery = em.createQuery(query).setParameter("name", name);

        try {
            user = typedQuery.getSingleResult();
            return user;
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public void update(UserEntity updatedUser) {
        UserEntity user = findById(updatedUser.getId());
        if (user != null) {
            em.persist(updatedUser);
            em.flush();
        }
    }

    public void deleteById(int id) {
        UserEntity user = findById(id);
        if (user != null) {
            em.remove(user);
            em.flush();
        }
    }

    public UserEntity validateCredentials(String name, String password) {
        UserEntity user = findByName(name);

        if (user == null) {
            return null;
        }
        else if (CryptoManager.validateMessage(password, user.getPassword())) {
            return user;
        }
        else {
            return null;
        }
    }

    //TODO: Check the provided password against a dictionary.
    public void create(UserEntity newUser) {
        if (findByName(newUser.getName()) != null) {
            throw new EntityExistsException();
        }

//        RoleName role = roleBean.getUserRole();
//        List<RoleName> roles = new ArrayList<RoleName>();
//        roles.add(em.merge(role));
//        user.setRoles(roles);

        newUser.setPassword(CryptoManager.hashMessage(newUser.getPassword()));

        em.persist(newUser);
        em.flush();
    }
}