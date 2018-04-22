package com.trivia.admin.controller;

import com.trivia.admin.resources.MessagesProducer;
import com.trivia.admin.utility.Messages;
import org.omnifaces.util.Faces;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.PropertyResourceBundle;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@Named
@ViewScoped
public class LogoutController implements Serializable {
    @Inject private transient FacesContext facesContext;
    @Inject private transient PropertyResourceBundle viewMessages;
    @Inject private SecurityContext securityContext;

    @PostConstruct
    public void init() {

    }

    public void logout() throws ServletException, IOException {
        Faces.logout();
        Messages.addWarnGlobalFlash(viewMessages.getString("warning"), viewMessages.getString("logout.message"));
        facesContext.getExternalContext().redirect("/public/login.xhtml");
    }
}
