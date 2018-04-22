package com.trivia.admin.controller;

import org.omnifaces.util.Faces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
@Named
@Dependent
public class ViewController {
    private String path;
    private String fullName;
    private String name;

    public ViewController() {}

    @PostConstruct
    public void init() {
        String viewId = Faces.getViewId();
        path = viewId.substring(1, viewId.lastIndexOf('.'));
        fullName = path.replaceFirst("WEB-INF/", "").replaceAll("\\W+", "_");
        name = fullName.substring(fullName.lastIndexOf('_') + 1, fullName.length());
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
}