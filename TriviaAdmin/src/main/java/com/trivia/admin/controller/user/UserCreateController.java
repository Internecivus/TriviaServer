package com.trivia.admin.controller.user;

import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Messages;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.service.RoleService;
import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.Role;
import com.trivia.persistence.entity.User;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import java.io.Serializable;
import java.util.List;


@Named
@ViewScoped
public class UserCreateController implements Serializable {
    private @Inject UserService userService;
    private @Inject RoleService roleService;
    private transient @Inject FacesContext facesContext;
    private User user;
    private List<Role> rolesAvailable;

    @PostConstruct
    public void init() {
        this.user = new User();
        this.rolesAvailable = roleService.getAll();
    }

    public String create() {
        userService.create(user);
        Messages.addInfoGlobalFlash(i18n.get("success"), i18n.get("create.success"));
        return facesContext.getViewRoot().getViewId() + "?faces-redirect=true";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getRolesAvailable() {
        return rolesAvailable;
    }
}