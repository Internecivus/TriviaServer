package com.trivia.core.service;

import com.trivia.core.exception.InvalidCredentialException;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.security.Cryptography;
import com.trivia.core.utility.Generator;
import com.trivia.persistence.entity.*;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;


@Stateless
@RolesAllowed(RoleType.Name.PRINCIPAL)
public class ClientService extends Service<Client> {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Resource SessionContext sessionContext;
    private @Inject Logger logger;
    private @Inject UserService userService;

    public ClientService() {
        super.DEFAULT_SORT_COLUMN = Client_.dateCreated;
        super.SEARCHABLE_COLUMNS = SORTABLE_COLUMNS = new ArrayList<>(Arrays.asList(Client_.dateCreated, Client_.id));
    }

    @RolesAllowed(RoleType.Name.PROVIDER)
    public Client create() {
        User user = userService.findByField(User_.name, sessionContext.getCallerPrincipal().getName());

        Client newClient = new Client();
        String apiKey;

        do {
            apiKey = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);
        }
        while (getByField(Client_.apiKey, apiKey) != null); // We could've easily have left this check out since the BLOB is large enough.
        String apiSecret = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);

        newClient.setApiKey(apiKey);
        newClient.setApiSecret(Cryptography.hashMessage(apiSecret));
        newClient.setDateCreated(new Timestamp(System.currentTimeMillis()));
        newClient.setUser(user);

        super.create(newClient);
        logger.info("Client id: {} was CREATED by user id: {}", newClient.getId(), sessionContext.getCallerPrincipal().getName());

        return newClient;
    }

    @PermitAll
    public Client register(String providerKey, String providerSecret) {
        userService.validateProvider(providerKey, providerSecret);
        return create();  // TODO: but this has RolesAllowed PROVIDER????
    }

    @PermitAll
    public Client validateCredential(String apiKey, String apiSecret) {
        Client user = findByField(Client_.apiKey, apiKey);
        if (Cryptography.validateMessage(apiSecret, user.getApiSecret())) {
            return user;
        }
        else {
            throw new InvalidCredentialException();
        }
    }

    @Override
    @RolesAllowed({ RoleType.Name.PROVIDER, RoleType.Name.ADMIN })
    public void update(Client updatedClient) {
        Client client = findById(updatedClient.getId());
        if (sessionContext.isCallerInRole(RoleType.Name.PROVIDER)) {
            User user = userService.getByField(User_.name, sessionContext.getCallerPrincipal().getName());
            if (user.isOwnerOf(client)) throw new NotAuthorizedException();
        }

        em.merge(client);
        em.flush();
        logger.info("Client id: {} was DELETED by user id: {}", updatedClient.getId(), sessionContext.getCallerPrincipal().getName());
    }

    @Override
    @RolesAllowed({ RoleType.Name.PROVIDER, RoleType.Name.ADMIN })
    public void deleteById(Object id) {
        Client client = findById(id);
        if (sessionContext.isCallerInRole(RoleType.Name.PROVIDER)) {
            User user = userService.getByField(User_.name, sessionContext.getCallerPrincipal().getName());
            if (user.isOwnerOf(client)) throw new NotAuthorizedException();
        }

        em.remove(client);
        em.flush();
        logger.info("Client id: {} was DELETED by user id: {}", id, sessionContext.getCallerPrincipal().getName());
    }
}
