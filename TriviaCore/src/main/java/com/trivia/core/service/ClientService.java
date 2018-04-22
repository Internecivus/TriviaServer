package com.trivia.core.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ClientService {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;

    public void createClient() {
        //create uuid
        //generate api key
        //generate secret
        //set user
    }

    public void registerClient(String clientId, String userApiKey) {

    }

    public void validateRequest(String request) {
        // get parts:
        // api key + secret
        // check if it exists and is registered under a user
    }
}
