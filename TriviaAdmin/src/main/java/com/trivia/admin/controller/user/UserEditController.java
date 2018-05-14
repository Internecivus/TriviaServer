package com.trivia.admin.controller.user;

import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Messages;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.service.RoleService;
import com.trivia.core.service.UserService;
import com.trivia.persistence.EntityView;
import com.trivia.persistence.entity.Role;
import com.trivia.persistence.entity.User;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;


@Named
@ViewScoped
public class UserEditController implements Serializable {
    private @Inject UserService userService;
    private @Inject RoleService roleService;
    private transient @Inject FacesContext facesContext;
    private User user;
    private Set<Role> rolesAvailable;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        int id = Integer.valueOf(request.getParameter("id"));
        this.user = userService.findById(id, EntityView.UserDetails);
        this.rolesAvailable = roleService.getAll();
    }

    public void delete() throws IOException {
        userService.deleteById(user.getId());
        Messages.addInfoFlashFor("growl", i18n.get("success"), i18n.get("delete.success"));
        facesContext.getExternalContext().redirect("/admin/users/list.xhtml");
    }

    public void update() {
        userService.update(user);
        Messages.addInfoGlobal(i18n.get("success"), i18n.get("update.success"));
        // TODO: Scroll to top
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Role> getRolesAvailable() {
        return rolesAvailable;
    }
}
