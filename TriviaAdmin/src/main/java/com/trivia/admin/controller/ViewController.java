package com.trivia.admin.controller;

import org.omnifaces.facesviews.FacesViews;
import org.omnifaces.util.Faces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;



@Named
@Dependent
public class ViewController {
    private @Inject SecurityContext securityContext;
    private @Inject FacesContext facesContext;

    private String path;
    private String fullName; // Uses dot ('.') as a delimiter.
    private String name;
    private String userName;

    private String[] folders = {"admin", "public"};

    public ViewController() {}

    @PostConstruct
    public void init() {
        path = getPathFromUri(Faces.getViewId());
        fullName = path
            .substring(1)
            .replaceFirst("WEB-INF/", "")
            .replaceFirst("(" + String.join("|", folders) + ")/", "")
            .replaceAll("/", ".");
        name = fullName.substring(fullName.lastIndexOf('.') + 1);

        if (securityContext.getCallerPrincipal() == null) {
            userName = null;
        }
        else {
            userName = securityContext.getCallerPrincipal().getName();
        }
    }

    public String getPathFromUri(String uri) {
        return uri.substring(0, uri.lastIndexOf('.'));
    }

    public String getPathFromOutcome(String outcome) {
        String pathWithoutName = path.substring(0, path.lastIndexOf(name) - 1);
        return pathWithoutName + "/" + outcome;
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