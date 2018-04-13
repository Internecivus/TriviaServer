package com.trivia.admin.utility;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
// Basically stolen from Omnifaces :-)
public class MessagesFacade {
    private static FacesContext facesContext = FacesContext.getCurrentInstance();


    public static void addGlobalErrorFlashMessage(String summary, String message) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, message);
    }



    private static void addFlashMessage(FacesMessage facesMessage) {
        facesContext.getExternalContext().getFlash();
        addGlobalMessage(facesMessage);
    }

    private static void addGlobalMessage(FacesMessage facesMessage) {
        facesContext.addMessage(null, facesMessage);
    }
}