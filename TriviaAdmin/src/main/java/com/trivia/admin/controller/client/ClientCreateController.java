package com.trivia.admin.controller.client;

import com.trivia.admin.controller.ViewController;
import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Messages;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.service.ClientService;
import com.trivia.core.service.RoleService;
import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.Client;
import com.trivia.persistence.entity.Role;
import com.trivia.persistence.entity.RoleType;
import com.trivia.persistence.entity.User;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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
    private transient @Inject ViewController viewController;
    private transient @Inject FacesContext facesContext;
    private String providerKey;
    private String providerSecret;

    @PostConstruct
    public void init() {}

    public void registerForCurrent() {
        Client client = clientService.registerForCurrent();
        Messages.addInfoGlobal(i18n.get("success"), i18n.get("create.success"));
        showSecretDialog(client.getApiSecret());
    }

    public void registerFor() {
        Client client;
        providerKey = providerKey.trim();
        try {
            if (viewController.getCurrentUser().hasRole(RoleType.ADMIN)) {
                client = clientService.registerFor(providerKey);
            }
            else {
                client = clientService.registerFor(providerKey, providerSecret);
            }
            Messages.addInfoGlobal(i18n.get("success"), i18n.get("create.success"));
            showSecretDialog(client.getApiSecret());
        }
        catch (NotAuthorizedException e) {
            Messages.addErrorGlobal(i18n.get("failure"),i18n.get("login.failure"));
            facesContext.validationFailed();
        }
    }

    private void showSecretDialog(String secret) {
        PrimeFaces.current().dialog().openDynamic();
    }

    //    p:dialog id="secretDialog" widgetVar="secretDialog" header="#{i18n['warning']}" resizable="false" dynamic="true">
//                <h:outputText value="#{i18n['auth.showApiSecret']}"/>
//            </p:dialog>

    public String getProviderKey() {
        return providerKey;
    }

    public void setProviderKey(String providerKey) {
        this.providerKey = providerKey;
    }

    public String getProviderSecret() {
        return providerSecret;
    }

    public void setProviderSecret(String providerSecret) {
        this.providerSecret = providerSecret;
    }
}