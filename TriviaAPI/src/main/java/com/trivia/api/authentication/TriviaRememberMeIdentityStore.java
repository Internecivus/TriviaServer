package com.trivia.api.authentication;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.RememberMeCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by faust. Part of TriviaServer Project. All rights reserved. 2018
 */
@ApplicationScoped
public class TriviaRememberMeIdentityStore implements RememberMeIdentityStore {
    @Inject private HttpServletRequest request;

    @Override
    public CredentialValidationResult validate(RememberMeCredential credential) {
        return null;
    }

    @Override
    public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
        return null;
    }

    @Override
    public void removeLoginToken(String loginToken) {

    }
}