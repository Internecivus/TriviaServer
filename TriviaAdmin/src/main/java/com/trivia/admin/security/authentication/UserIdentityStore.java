package com.trivia.admin.security.authentication;

import com.trivia.core.exception.BusinessException;
import com.trivia.core.exception.CredentialException;
import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
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

        try {
            user = userService.validateCredential(username, password);
        }
        catch (CredentialException e) {
            return CredentialValidationResult.INVALID_RESULT;
        }
        catch (BusinessException e) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }

        return new CredentialValidationResult(new UserCallerPrincipal(user), user.getRolesNames());
    }
}