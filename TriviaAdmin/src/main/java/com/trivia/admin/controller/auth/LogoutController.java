package com.trivia.admin.controller.auth;

import com.trivia.admin.utility.Message;
import org.omnifaces.util.Faces;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import com.trivia.admin.resources.i18n;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@Named
@ViewScoped
public class LogoutController implements Serializable {
    @Inject private transient FacesContext facesContext;
    @Inject private SecurityContext securityContext;

    @PostConstruct
    public void init() {

    }

    public void logout() throws ServletException, IOException {
        Faces.logout();
        Message.addWarnGlobalFlash(i18n.get("warning"), i18n.get("logout.message"));
        facesContext.getExternalContext().redirect("/public/login.xhtml");
    }
}
