package com.trivia.admin.utility;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
// This is basically stolen from Omnifaces :-)
// The only reason why we are using this instead of Omnifaces is that we want both details and the summary of the messages.
public class Messages {
    private static FacesMessage facesMessage;

    public static void addErrorGlobalFlash(String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, message);
        addGlobalFlash();
    }

    public static void addFatalGlobalFlash(String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, message);
        addGlobalFlash();
    }

    public static void addInfoGlobalFlash(String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, message);
        addGlobalFlash();
    }

    public static void addWarnGlobalFlash(String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, summary, message);
        addGlobalFlash();
    }

    public static void addErrorGlobal(String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, message);
        addGlobal();
    }

    public static void addInfoGlobal(String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, message);
        addGlobal();
    }

    public static void addInfoFor(String clientId, String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, message);
        addFor(clientId);
    }

    public static void addWarnFor(String clientId, String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, summary, message);
        addFor(clientId);
    }

    public static void addErrorFor(String clientId, String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, message);
        addFor(clientId);
    }

    public static void addFatalFor(String clientId, String summary, String message) {
        facesMessage = new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, message);
        addFor(clientId);
    }

    private static void addGlobalFlash() {
        addFlash();
        addGlobal();
    }

    private static void addGlobal() {
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    private static void addFor(String clientId) {
        FacesContext.getCurrentInstance().addMessage(clientId, facesMessage);
    }

    public static void addFlash() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }
}