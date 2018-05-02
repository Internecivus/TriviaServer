package com.trivia.admin.controller;

import org.omnifaces.util.Faces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@Named
@Dependent
public class ViewController {
    private @Inject SecurityContext securityContext;
    private @Inject FacesContext facesContext;

    private String path;
    private String fullName;
    private String name;
    private String userName;

    public ViewController() {}

    @PostConstruct
    public void init() {
        String viewId = Faces.getViewId();
        path = viewId.substring(1, viewId.lastIndexOf('.'));
        fullName = path.replaceFirst("WEB-INF/", "").replaceAll("\\W+", "_");
        name = fullName.substring(fullName.lastIndexOf('_') + 1, fullName.length());

        if (securityContext.getCallerPrincipal() == null) {
            userName = "";
        }
        else {
            userName = securityContext.getCallerPrincipal().getName();
        }
    }

    public boolean pathIs(String path) {
        return (path.equals(this.path));
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }
}