package com.trivia.core.services;

import com.trivia.core.exception.InvalidCredentialsException;
import com.trivia.core.exception.SystemException;
import com.trivia.core.security.CryptoManager;
import com.trivia.persistence.entity.UserEntity;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Stateless
public class UserBean {
    @PersistenceContext(unitName = "MorbidTriviaDB")
    private EntityManager em;
    private UserEntity userEntity;

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public UserEntity findById(int id) {
        return em.find(UserEntity.class, id);
    }

    public UserEntity findByName(String name) {
        UserEntity user;

        TypedQuery<UserEntity> query = em.createQuery("SELECT u FROM UserEntity u WHERE u.name = name", UserEntity.class);
        user = query.getSingleResult();

        return user;
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


        if(CryptoManager.validateMessage(password, user.getPassword())) {
            return user;
        }


        return null;
    }

    //TODO: Check the provided password against a dictionary.
    public UserEntity create(UserEntity newUser) {
        if (findByName(newUser.getName()) == null) {
            return null;
        }

//        Role role = roleBean.getUserRole();
//        List<Role> roles = new ArrayList<Role>();
//        roles.add(em.merge(role));
//        user.setRoles(roles);


        newUser.setPassword(CryptoManager.hashMessage(newUser.getPassword()));
        

        em.persist(newUser);
        em.flush();

        return newUser;
    }
}