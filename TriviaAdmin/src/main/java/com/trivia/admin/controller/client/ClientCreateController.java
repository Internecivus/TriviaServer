package com.trivia.admin.controller.client;

import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Messages;
import com.trivia.core.service.ClientService;
import com.trivia.core.service.RoleService;
import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.Client;
import com.trivia.persistence.entity.Role;
import com.trivia.persistence.entity.User;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Set;



@Named
@ViewScoped
public class ClientCreateController implements Serializable {
    private @Inject ClientService clientService;
    private @Inject RoleService roleService;
    private transient @Inject FacesContext facesContext;
    private Client client;

    @PostConstruct
    public void init() {
        this.client = new Client();
    }

    public String create() {
        clientService.create(client);
        Messages.addInfoGlobalFlash(i18n.get("success"), i18n.get("create.success"));
        return facesContext.getViewRoot().getViewId() + "?faces-redirect=true";
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}