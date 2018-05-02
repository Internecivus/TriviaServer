package com.trivia.core.service;

import com.trivia.core.exception.*;
import com.trivia.core.exception.EntityExistsException;
import com.trivia.core.exception.EntityNotFoundException;
import com.trivia.core.security.Cryptography;
import com.trivia.core.utility.Generator;
import com.trivia.core.utility.SortOrder;
import com.trivia.persistence.entity.*;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.xml.registry.infomodel.User;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Stateless
public class UserService extends Service<UserEntity> {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Resource SessionContext sessionContext;
    private @Inject Logger logger;

    public UserService() {
        super.PAGE_SIZE_DEFAULT = 100;
        super.PAGE_SIZE_MAX = 20;
        super.DEFAULT_SORT_COLUMN = UserEntity_.dateCreated;
        super.SEARCHABLE_COLUMNS = SORTABLE_COLUMNS = new ArrayList<>(Arrays.asList(UserEntity_.name, UserEntity_.id));
    }

    private UserEntity promoteToProvider(UserEntity user) {
        if (!sessionContext.isCallerInRole(Role.ADMIN.toString())) throw new NotAuthorizedException();
        if (user.hasRole(Role.PROVIDER)) throw new EntityExistsException(); // User already is a provider.

        String providerKey = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);
        String providerSecret = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);

        user.setProviderKey(providerKey);
        user.setProviderSecret(Cryptography.hashMessage(providerSecret));

        return user;
    }

    private void resetProvider() {

    }

    public void deleteById(int id) {
        if (!sessionContext.isCallerInRole(Role.ADMIN.toString())) throw new NotAuthorizedException();
        super.deleteById(id);
    }

    public UserEntity validateProvider(String providerKey, String providerSecret) {
        UserEntity user = findByField(UserEntity_.providerKey, providerKey);
        if (Cryptography.validateMessage(providerSecret, user.getProviderSecret())) {
            return user;
        }
        else {
            throw new InvalidCredentialException();
        }
    }

    public void update(UserEntity updatedUser) {
        // We check if we only just now added the role of Provider. Hacky, but does the trick.
        if (updatedUser.hasRole(Role.PROVIDER) && !findById(updatedUser.getId()).hasRole(Role.PROVIDER)) {
            updatedUser = promoteToProvider(updatedUser);
        }

        super.update(updatedUser);
    }

    public UserEntity validateCredential(String name, String password) {
        UserEntity user = findByField(UserEntity_.name, name);
        if (Cryptography.validateMessage(password, user.getPassword())) {
            return user;
        }
        else {
            throw new InvalidCredentialException();
        }
    }

    public void create(UserEntity newUser) {
        if (getByField(UserEntity_.name, newUser.getName()) != null) throw new EntityExistsException();
        if (newUser.hasRole(Role.PROVIDER)) {
            newUser = promoteToProvider(newUser);
        }
        newUser.setDateCreated(new Timestamp(System.currentTimeMillis()));
        newUser.setPassword(Cryptography.hashMessage(newUser.getPassword()));

        super.create(newUser);
    }
}