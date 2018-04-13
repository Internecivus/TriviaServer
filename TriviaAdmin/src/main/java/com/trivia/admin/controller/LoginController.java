package com.trivia.admin.controller;

import com.trivia.core.services.UserBean;

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
    private String name;
    private String password;

    @PostConstruct
    public void init() {

    }

    public void login() {
        userBean.validateCredentials(name, password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
