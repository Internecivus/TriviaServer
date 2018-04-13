package com.trivia.admin.resources;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import java.util.PropertyResourceBundle;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
// TODO: This is not at all typesafe?
public class MessagesProducer {
    @Produces
    public PropertyResourceBundle getBundle() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().evaluateExpressionGet(context, "#{viewMessages}", PropertyResourceBundle.class);
    }
}