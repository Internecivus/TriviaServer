package com.trivia.persistence.entity;


/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
public enum Role {
    CONTRIBUTOR("CONTRIBUtor"),
    PROVIDER("PROVIDER"),
    MODERATOR("MODERATOR"),
    ADMIN("ADMIN");

    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    }