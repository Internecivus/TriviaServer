package com.trivia.api.security.authentication;

import com.trivia.persistence.entity.ClientEntity;
import com.trivia.persistence.entity.UserEntity;

import javax.security.enterprise.CallerPrincipal;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
public class ClientCallerPrincipal extends CallerPrincipal {
    private final ClientEntity client;

    public ClientCallerPrincipal(ClientEntity client) {
        super(client.getApiKey());
        this.client = client;
    }

    public ClientEntity getClient() {
        return client;
    }
}
