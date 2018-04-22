package com.trivia.admin.controller;

import com.trivia.admin.utility.Messages;
import com.trivia.core.exception.BusinessException;
import com.trivia.persistence.entity.UserEntity;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Named
@ViewScoped
public class LoginController implements Serializable {
    @Inject private FacesContext facesContext;
    @Inject private SecurityContext securityContext;
    private UserEntity userEntity;
    private boolean rememberMe;

    @PostConstruct
    public void init() {
        userEntity = new UserEntity();
    }

    public void login() {

        try {
            AuthenticationStatus authenticationStatus = securityContext.authenticate(
                    (HttpServletRequest) facesContext.getExternalContext().getRequest(),
                    (HttpServletResponse) facesContext.getExternalContext().getResponse(),
                    AuthenticationParameters.withParams()
                            .credential(new UsernamePasswordCredential(userEntity.getName(), userEntity.getPassword()))
                            .newAuthentication(true)
                            .rememberMe(rememberMe)
            );

            if (authenticationStatus == AuthenticationStatus.SEND_FAILURE) {
                Messages.addErrorGlobal("Failed", "Authentication failed.");
                facesContext.validationFailed();
            } else if (authenticationStatus == AuthenticationStatus.SEND_CONTINUE) {
                facesContext.responseComplete();
            }
        }
        catch (BusinessException e) {
            Messages.addErrorGlobal("Failed", "Authentication failed.");
        }
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
