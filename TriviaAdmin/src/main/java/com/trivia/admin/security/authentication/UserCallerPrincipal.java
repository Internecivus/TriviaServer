package com.trivia.admin.security.authentication;

import com.trivia.persistence.entity.UserEntity;

import javax.security.enterprise.CallerPrincipal;



public class UserCallerPrincipal extends CallerPrincipal {
    private final UserEntity user;

    public UserCallerPrincipal(UserEntity user) {
        super(user.getName());
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }
}
