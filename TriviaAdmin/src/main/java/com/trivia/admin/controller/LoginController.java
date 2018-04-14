package com.trivia.admin.controller;

import com.trivia.core.services.UserBean;
import com.trivia.persistence.entity.UserEntity;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Named
@ViewScoped
public class LoginController implements Serializable {
    @Inject private UserBean userBean;
    private UserEntity user;
    private boolean rememberMe;

    @PostConstruct
    public void init() {

    }

    public void login() {
        userBean.validateCredentials(user.getName(), user.getPassword());
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
