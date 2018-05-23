package com.trivia.core.service;

import com.trivia.core.exception.*;
import com.trivia.core.exception.EntityExistsException;
import com.trivia.core.exception.EntityNotFoundException;
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
import javax.management.relation.Role;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * There are many extra User selects and DRY violations going on here, and in the future, a custom RolesAllowed
 * annotation (and figuring out how to get the CallerPrincipal entities without an interceptor)
 * and special entity graphs could fix this.
 */
@Stateless
@RolesAllowed(RoleType.Name.MODERATOR)
public class UserService extends Service<User> {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Resource SessionContext sessionContext;
    private @Inject Logger logger;

    public UserService() {
        super.DEFAULT_SORT_COLUMN = User_.dateCreated;
        super.SEARCHABLE_COLUMNS = SORTABLE_COLUMNS = new HashSet<>(Arrays.asList(User_.name, User_.id, User_.dateCreated, User_.providerKey));
    }

    @Override
    @RolesAllowed(RoleType.Name.ADMIN)
    public void deleteById(Object id) {
        super.deleteById(id);
        logger.info("User id: {} was DELETED by user: {}", id, sessionContext.getCallerPrincipal().getName());
    }

    @PermitAll
    public User validateProvider(String providerKey, String providerSecret) {
        User user = findByField(User_.providerKey, providerKey);
        if (Cryptography.validateMessage(providerSecret, user.getProviderSecret())) {
            return user;
        }
        else {
            throw new InvalidCredentialException();
        }
    }

    private User demoteFromProvider(User user) {
        // User is not a provider.
        if (user.getProviderKey() == null) throw new EntityNotFoundException();

        user.setProviderKey(null);
        user.setProviderSecret(null);

        return user;
    }

    private User promoteToProvider(User user) {
        // User already is a provider.
        if (user.getProviderKey() != null) throw new EntityNotFoundException();

        String providerKey = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);
        String providerSecret = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);

        user.setProviderKey(providerKey);
        user.setProviderSecret(providerSecret);

        return user;
    }

    /**
     * providerSecret will be set to null if it was not updated.
     */
    @Override
    @RolesAllowed(RoleType.Name.USER)
    public User update(User updatedUser) {
        User oldUser = findById(updatedUser.getId(), EntityView.UserDetails);
        User prePersistUser = new User(updatedUser);
        prePersistUser.setProviderSecret(null);

        // We check if we only just now added the role of Provider, or if we just removed it. Hacky, but does the trick.
        if (updatedUser.hasRole(RoleType.PROVIDER) && !oldUser.hasRole(RoleType.PROVIDER)) {
            updatedUser = promoteToProvider(updatedUser);
            // TODO: This is violating the SRP.
            prePersistUser.setProviderSecret(updatedUser.getProviderSecret());
            updatedUser.setProviderSecret(Cryptography.hashMessage(updatedUser.getProviderSecret()));
        }
        else if (!updatedUser.hasRole(RoleType.PROVIDER) && oldUser.hasRole(RoleType.PROVIDER)) {
            updatedUser = demoteFromProvider(updatedUser);
        }

        // Check if we need to update the password too.
        if (!oldUser.getPassword().equals(updatedUser.getPassword())) updatePassword(updatedUser);

        em.merge(updatedUser);
        em.flush();

        logger.info("User id: {} was UPDATED by user: {}", updatedUser.getId(), sessionContext.getCallerPrincipal().getName());

        return prePersistUser;
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

    @RolesAllowed(RoleType.Name.USER)
    private void updatePassword(User user) {
        // If the calling user is not an admin, it can only change its own password.
        if (!user.hasRole(RoleType.ADMIN)) {
            User callingUser = getByField(User_.name, sessionContext.getCallerPrincipal().getName());
            if (!callingUser.getId().equals(user.getId())) throw new NotAuthorizedException();
        }

        user.setPassword(Cryptography.hashMessage(user.getPassword()));
    }

    @Override
    public User create(User newUser) {
        if (getByField(User_.name, newUser.getName()) != null) throw new EntityExistsException();

        User prePersistUser = new User(newUser);

        if (newUser.hasRole(RoleType.PROVIDER)) {
            newUser = promoteToProvider(newUser);
            // TODO: This is violating the SRP.
            prePersistUser.setProviderSecret(newUser.getProviderSecret());
            newUser.setProviderSecret(Cryptography.hashMessage(newUser.getProviderSecret()));
        }
        else if (newUser.hasRole(RoleType.ADMIN)) {
            newUser = promoteToAdmin(newUser);
        }

        newUser.setPassword(Cryptography.hashMessage(newUser.getPassword()));

        super.create(newUser);

        logger.info("User id: {} was CREATED by user: {}", newUser.getId(), sessionContext.getCallerPrincipal().getName());
        return prePersistUser;
    }

    // We could check authorization programmatically but maybe we will be able to use this method later
    @RolesAllowed(RoleType.Name.ADMIN)
    private User promoteToAdmin(User admin) {
        return admin;
    }
}