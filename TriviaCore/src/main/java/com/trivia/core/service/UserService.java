package com.trivia.core.service;

import com.trivia.core.exception.*;
import com.trivia.core.exception.EntityExistsException;
import com.trivia.core.security.Cryptography;
import com.trivia.core.utility.Generator;
import com.trivia.persistence.EntityView;
import com.trivia.persistence.entity.*;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@Stateless
@RolesAllowed({ RoleType.Name.MODERATOR, RoleType.Name.ADMIN })
public class UserService extends Service<User> {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Resource SessionContext sessionContext;
    private @Inject Logger logger;

    public UserService() {
        super.DEFAULT_SORT_COLUMN = User_.dateCreated;
        super.SEARCHABLE_COLUMNS = SORTABLE_COLUMNS = new HashSet<>(Arrays.asList(User_.name, User_.id, User_.dateCreated, User_.providerKey));
    }

    private void resetProvider() {

    }

    @Override
    @RolesAllowed(RoleType.Name.ADMIN)
    public void deleteById(Object id) {
        super.deleteById(id);
        logger.info("User id: {} was DELETED by user: {}", id, sessionContext.getCallerPrincipal().getName());
    }

    public User validateProvider(String providerKey, String providerSecret) {
        User user = findByField(User_.providerKey, providerKey);
        if (Cryptography.validateMessage(providerSecret, user.getProviderSecret())) {
            return user;
        }
        else {
            throw new InvalidCredentialException();
        }
    }

    @RolesAllowed(RoleType.Name.MODERATOR)
    private User promoteToProvider(User provider) {
        if (provider.hasRole(RoleType.PROVIDER)) throw new EntityExistsException(); // User already is a provider.

        String providerKey = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);
        String providerSecret = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);

        provider.setProviderKey(providerKey);
        provider.setProviderSecret(Cryptography.hashMessage(providerSecret));

        return provider;
    }

    @Override
    @RolesAllowed(RoleType.Name.ADMIN)
    public void update(User updatedUser) {
        // We check if we only just now added the role of Provider. Hacky, but does the trick.
        if (updatedUser.hasRole(RoleType.PROVIDER) && !findById(updatedUser.getId()).hasRole(RoleType.PROVIDER)) {
            updatedUser = promoteToProvider(updatedUser);
        }

        logger.info("User id: {} was UPDATED by user: {}", updatedUser.getId(), sessionContext.getCallerPrincipal().getName());
        super.update(updatedUser);
    }

    @PermitAll
    public User validateCredential(String name, String password) {
        User user = findByField(User_.name, name, EntityView.UserDetails);
        if (Cryptography.validateMessage(password, user.getPassword())) {
            return user;
        }
        else {
            throw new InvalidCredentialException();
        }
    }

    @Override
    @RolesAllowed({RoleType.Name.MODERATOR, RoleType.Name.ADMIN}) // Because an admin who is not a moderator is still authorized to add roles.
    public User create(User newUser) {
        if (getByField(User_.name, newUser.getName()) != null) throw new EntityExistsException();
        if (newUser.hasRole(RoleType.PROVIDER)) {
            newUser = promoteToProvider(newUser);
        }
        else if (newUser.hasRole(RoleType.ADMIN)) {
            newUser = promoteToAdmin(newUser);
        }

        newUser.setPassword(Cryptography.hashMessage(newUser.getPassword()));

        super.create(newUser);
        logger.info("User id: {} was CREATED by user: {}", newUser.getId(), sessionContext.getCallerPrincipal().getName());
        return newUser;
    }

    // We could check authorization programmatically but maybe we will be able to use this method later
    @RolesAllowed(RoleType.Name.ADMIN)
    private User promoteToAdmin(User admin) {
        return admin;
    }
}