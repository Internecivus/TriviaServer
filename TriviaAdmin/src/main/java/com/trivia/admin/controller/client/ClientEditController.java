package com.trivia.admin.controller.client;

import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Messages;
import com.trivia.core.service.ClientService;
import com.trivia.core.service.RoleService;
import com.trivia.core.service.UserService;
import com.trivia.persistence.EntityView;
import com.trivia.persistence.entity.Client;
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
import java.util.Set;


@Named
@ViewScoped
public class ClientEditController implements Serializable {
    private @Inject ClientService clientService;
    private transient @Inject FacesContext facesContext;
    private Client client;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        int id = Integer.valueOf(request.getParameter("id"));
        this.client = clientService.findById(id, EntityView.ClientDetails);
    }

    public void generateNewAPISecret() {
        clientService.generateNewAPISecret(client);
        PrimeFaces.current().scrollTo("messages");
    }

    public void delete() throws IOException {
        clientService.deleteById(client.getId());
        Messages.addWarnFlashFor("growl", i18n.get("success"), i18n.get("delete.success"));
        facesContext.getExternalContext().redirect("/admin/clients/list.xhtml");
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
