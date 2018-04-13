package com.trivia.admin.controller;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@Named
@ViewScoped
public class LogoutController implements Serializable {
    @PostConstruct
    public void init() {

    }

    public void logout() {

    }
}
