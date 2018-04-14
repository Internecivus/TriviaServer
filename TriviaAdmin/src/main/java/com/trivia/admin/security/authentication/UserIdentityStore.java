package com.trivia.admin.security.authentication;

import com.trivia.core.exception.CredentialsException;
import com.trivia.core.services.UserBean;
import com.trivia.core.exception.InvalidGroupException;
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
//TODO: Reset password via email, using a nonce random binary blob token valid for 15 min tied to the account via a special table.
//TODO: Creation and logging in should be done via two different database users with different permissions (create vs read)
@ApplicationScoped
public class UserIdentityStore implements IdentityStore {
    @Inject private UserBean userBean;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        UserEntity userEntity = null;

        if (credential instanceof UsernamePasswordCredential) {
            String username = ((UsernamePasswordCredential) credential).getCaller();
            String password = ((UsernamePasswordCredential) credential).getPasswordAsString();
            userEntity = userBean.validateCredentials(username, password);
        }
        else if (credential instanceof CallerOnlyCredential) {
            String username = ((CallerOnlyCredential) credential).getCaller();
            userEntity = userBean.findByName(username);
        }

        return validate(userEntity);
    }

    static CredentialValidationResult validate(UserEntity user) {
        if (user == null) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }

        try {
            if (user.getRole().getName() != ("USER")) {
                throw new InvalidGroupException();
            }
//          if (!user.isEmailVerified()) {
//                throw new EmailNotVerifiedException();
//            }

            return new CredentialValidationResult(new UserCallerPrincipal(user));//user.getRolesAsStrings());
        }
        catch (CredentialsException e) {
            return CredentialValidationResult.INVALID_RESULT;
        }
    }
}