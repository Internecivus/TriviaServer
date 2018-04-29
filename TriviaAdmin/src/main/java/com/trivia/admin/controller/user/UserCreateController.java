package com.trivia.admin.controller.user;

import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.Role;
import com.trivia.persistence.entity.UserEntity;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class UserCreateController implements Serializable {
    private @Inject UserService userService;
    private UserEntity userEntity;
    private Role roleNames;

    @PostConstruct
    public void init() {
        this.userEntity = new UserEntity();
       // this.categoriesAvailable = user.getAll(); TODO:ROLES
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Role getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Role roleNames) {
        this.roleNames = roleNames;
    }
}
