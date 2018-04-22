package com.trivia.admin.security.authentication;

import com.trivia.persistence.entity.UserEntity;

import javax.security.enterprise.CallerPrincipal;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
public class AdminCallerPrincipal extends CallerPrincipal {
    private final UserEntity user;

    public AdminCallerPrincipal(UserEntity user) {
        super(user.getName());
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }
}