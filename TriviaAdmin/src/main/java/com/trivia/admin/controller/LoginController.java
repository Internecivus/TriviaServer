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
import java.io.IOException;
import java.io.Serializable;
import java.util.PropertyResourceBundle;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Named
@ViewScoped
public class LoginController implements Serializable {
    @Inject private FacesContext facesContext;
    @Inject private SecurityContext securityContext;
    @Inject private transient PropertyResourceBundle viewMessages;
    private UserEntity userEntity;
    private boolean rememberMe;

    @PostConstruct
    public void init() throws IOException {
        userEntity = new UserEntity();

        if (securityContext.getCallerPrincipal() != null) {
            facesContext.getExternalContext().redirect("/admin/index.xhtml");
        }
    }

    public void login() throws IOException {
        AuthenticationStatus authenticationStatus = securityContext.authenticate(
                (HttpServletRequest) facesContext.getExternalContext().getRequest(),
                (HttpServletResponse) facesContext.getExternalContext().getResponse(),
                AuthenticationParameters.withParams()
                        .credential(new UsernamePasswordCredential(userEntity.getName(), userEntity.getPassword()))
                        .newAuthentication(true)
                        .rememberMe(rememberMe)
        );

        if (authenticationStatus == AuthenticationStatus.SEND_FAILURE) {
            Messages.addErrorGlobal(viewMessages.getString("failure"),viewMessages.getString("login.failure"));
            facesContext.validationFailed();
        }
        else if (authenticationStatus == AuthenticationStatus.SEND_CONTINUE) {
            facesContext.responseComplete();
        }
        else if (authenticationStatus == AuthenticationStatus.SUCCESS) {
            facesContext.getExternalContext().redirect("/admin/index.xhtml");
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
