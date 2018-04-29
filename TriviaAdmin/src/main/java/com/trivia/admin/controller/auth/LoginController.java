package com.trivia.admin.controller.auth;

import com.trivia.admin.utility.Message;
import com.trivia.persistence.entity.UserEntity;
import com.trivia.admin.resources.i18n;

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
    // For some way this throws a warning about init() using IOException, but a return of String (as in using the String
    // redirect) is not even going to deploy. Is init() not supposed to redirect?
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

        if (authenticationStatus.equals(AuthenticationStatus.SEND_FAILURE)) {
            Message.addErrorGlobal(i18n.get("failure"),i18n.get("login.failure"));
            facesContext.validationFailed();
        }
        else if (authenticationStatus.equals(AuthenticationStatus.SEND_CONTINUE)) {
            facesContext.responseComplete();
        }
        else if (authenticationStatus.equals(AuthenticationStatus.SUCCESS)) {
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
