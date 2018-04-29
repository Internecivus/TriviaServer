package com.trivia.admin.security.authentication;

import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;



@ApplicationScoped
public class UserIdentityStore implements IdentityStore {
    @Inject private UserService userService;

    public CredentialValidationResult validate(UsernamePasswordCredential credential) {
        UserEntity user;

        String username = credential.getCaller();
        String password = credential.getPasswordAsString();
        user = userService.validateCredential(username, password);

        if (user == null) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        return new CredentialValidationResult(new UserCallerPrincipal(user), user.getRolesNames());
    }
}