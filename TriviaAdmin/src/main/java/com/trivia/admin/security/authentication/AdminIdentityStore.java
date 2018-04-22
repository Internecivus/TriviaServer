package com.trivia.admin.security.authentication;

import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@ApplicationScoped
public class AdminIdentityStore implements IdentityStore {
    @Inject private UserService userService;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        UserEntity userEntity = null;

        if (credential instanceof UsernamePasswordCredential) {
            String username = ((UsernamePasswordCredential) credential).getCaller();
            String password = ((UsernamePasswordCredential) credential).getPasswordAsString();
            userEntity = userService.validateCredentials(username, password);
        }
        else if (credential instanceof CallerOnlyCredential) {
            String username = ((CallerOnlyCredential) credential).getCaller();
            userEntity = userService.findByName(username);
        }

        return validate(userEntity);
    }

    static private CredentialValidationResult validate(UserEntity user) {
        if (user == null) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }

        return new CredentialValidationResult(new AdminCallerPrincipal(user), user.getRolesNames());

        //return CredentialValidationResult.INVALID_RESULT;
    }
}