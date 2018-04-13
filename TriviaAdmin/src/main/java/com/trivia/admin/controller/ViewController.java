package com.trivia.admin.controller;

import org.primefaces.config.WebXmlParser;

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
    @Inject private FacesContext facesContext;
    private String path;
    private String name;

    public ViewController() {}

    @PostConstruct
    public void init() {
        String viewId = facesContext.getViewRoot().getViewId();
        path = viewId.substring(1, viewId.lastIndexOf('.'));
        name = path.replaceFirst("WEB\\-INF/", "").replaceAll("\\W+", "_");
    }

    public boolean pathIs(String path) {
        return (path == this.path);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}