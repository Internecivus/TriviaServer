package com.trivia.core.service;

import com.trivia.core.exception.EntityNotFoundException;
import com.trivia.core.exception.InvalidCredentialException;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.security.Cryptography;
import com.trivia.core.utility.Generator;
import com.trivia.persistence.entity.*;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.security.enterprise.SecurityContext;
import java.sql.Timestamp;
import java.util.Base64;

@Stateless
public class ClientService {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Resource SessionContext sessionContext;
    private @Inject Logger logger;
    private @Inject UserService userService;

    public ClientEntity create() {
        if (!sessionContext.isCallerInRole(Role.ADMIN.toString())) throw new NotAuthorizedException();
        UserEntity user = userService.findByField(UserEntity_.name, sessionContext.getCallerPrincipal().getName());

        ClientEntity clientEntity = new ClientEntity();
        String apiKey;

        do {
            apiKey = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);
        }
        while (getByApiKey(apiKey) != null); // We could've easily have left this check out since the BLOB is large enough.
        String apiSecret = Generator.generateSecureRandomString(Cryptography.API_KEY_LENGTH);

        clientEntity.setApiKey(apiKey);
        clientEntity.setApiSecret(Cryptography.hashMessage(apiSecret));
        clientEntity.setDateCreated(new Timestamp(System.currentTimeMillis()));
        clientEntity.setUser(user);

        em.persist(clientEntity);
        em.flush();
        logger.info("Client id: {} CREATED by user id: {}", clientEntity.getId(), user.getId());

        return clientEntity;
    }

    public ClientEntity register(String providerKey, String providerSecret) {
        userService.validateProvider(providerKey, providerSecret);
        return create();
    }

    public ClientEntity findByApiKey(String apiKey) {
        ClientEntity user = getByApiKey(apiKey);
        if (user == null) throw new EntityNotFoundException();
        return user;
    }

    private ClientEntity getByApiKey(String apiKey) {
        ClientEntity client;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ClientEntity> query = builder.createQuery(ClientEntity.class);
        Root<ClientEntity> root = query.from(ClientEntity.class);

        // TODO: Not sure if we want this to be optimised (it reuses the query if the param is the same type) because of security concerns.
        ParameterExpression<String> nameParameter = builder.parameter(String.class, ClientEntity_.API_KEY);
        query.select(root).where(builder.equal(root.get(ClientEntity_.API_KEY), nameParameter));
        TypedQuery<ClientEntity> typedQuery = em.createQuery(query).setParameter(ClientEntity_.API_KEY, apiKey);

        try {
            client = typedQuery.getSingleResult();
        }
        catch (NoResultException e) {
            client = null;
        }

        return client;
    }

    public ClientEntity validateCredential(String apiKey, String apiSecret) {
        ClientEntity user = findByApiKey(apiKey);
        if (Cryptography.validateMessage(apiSecret, user.getApiSecret())) {
            return user;
        }
        else {
            throw new InvalidCredentialException();
        }
    }
}
