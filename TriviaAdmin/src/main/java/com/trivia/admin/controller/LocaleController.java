package com.trivia.admin.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */

// TODO: This is very bare-bones. Locale should change not only on user action but via cookies. Reference implementation - Kickoff)

@Named
@SessionScoped
public class LocaleController implements Serializable {
    private Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();

    @PostConstruct
    public void init() {

    }

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void setLanguage(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }
}