package com.trivia.api.security.authentication;

import com.trivia.core.service.ClientService;
import com.trivia.persistence.entity.ClientEntity;
import com.trivia.persistence.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.*;


@ApplicationScoped
public class ClientIdentityStore implements IdentityStore {
    @Inject private ClientService clientService;

    public CredentialValidationResult validate(UsernamePasswordCredential credential) {
        ClientEntity client;

        String username = credential.getCaller();
        String password = credential.getPasswordAsString();
        client = clientService.validateCredential(username, password);

        if (client == null) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        return new CredentialValidationResult(new ClientCallerPrincipal(client), new HashSet<>(Arrays.asList("CLIENT")));
    }
}