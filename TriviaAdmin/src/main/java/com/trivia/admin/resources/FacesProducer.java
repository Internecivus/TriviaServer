package com.trivia.admin.resources;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

public class FacesProducer {
    //TODO: Maybe needs RequestScoped?
    @RequestScoped
    @Produces
    public FacesContext produceFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
