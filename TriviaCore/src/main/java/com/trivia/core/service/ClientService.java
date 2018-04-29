package com.trivia.core.service;

import com.trivia.core.utility.Generator;
import com.trivia.persistence.entity.ClientEntity;
import com.trivia.persistence.entity.UserEntity;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.SecurityContext;
import java.util.Base64;

@Stateless
public class ClientService {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Resource SessionContext securityContext;
    private @Inject UserService userService;

    public void create() {
        ClientEntity clientEntity = new ClientEntity();
        String clientId = Base64.getEncoder().encodeToString(Generator.generateSecureRandomBytes(32));
        String apiKey = Base64.getEncoder().encodeToString(Generator.generateSecureRandomBytes(32));
        String apiSecret = Base64.getEncoder().encodeToString(Generator.generateSecureRandomBytes(32));
        UserEntity user = userService.findByName(securityContext.getCallerPrincipal().getName());

        clientEntity.setClientId(clientId);
        clientEntity.setApiKey(apiKey);
        clientEntity.setApiSecret(apiSecret);

    }



    public void validateProvider(String providerId, String providerSecret) {

    }

    public void register(String clientId, String userApiKey) {

    }

    public ClientEntity findByClientId(String clientId) {
        return null;
    }

    public ClientEntity validateCredential(String apiKey, String apiSecret) {
        //check if everything is fine


        return null;
    }
}
