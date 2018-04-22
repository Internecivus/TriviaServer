package com.trivia.admin.security.authentication;

import com.trivia.core.service.UserBean;
import com.trivia.core.exception.CredentialsException;
import com.trivia.persistence.entity.RoleEntity;
import com.trivia.persistence.entity.RoleName;
import com.trivia.persistence.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@ApplicationScoped
public class AdminIdentityStore implements IdentityStore {
    @Inject private UserBean userBean;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        //TODO: THIS IS TEMPORARY!
        RoleEntity role = new RoleEntity();
        role.setName(RoleName.ADMIN);
        UserEntity user = new UserEntity(); user.setName("admin"); user.setPassword("password"); user.addRole(role); userBean.create(user);

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

    static private CredentialValidationResult validate(UserEntity user) {
        if (user == null) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }

        return new CredentialValidationResult(new AdminCallerPrincipal(user), user.getRolesNames());

        //return CredentialValidationResult.INVALID_RESULT;
    }
}