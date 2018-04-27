package com.trivia.admin.resources;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
public final class i18n {
    private i18n() {

    }

    //TODO: Check the performance of fetching via EL and util
    /** It's unfortunate that IntelliJ does not autofetch the key-value pairs if we use this method.
     * If you want this, create a direct PropertyResourceBundle injection for example.
     * */
    public static String get(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context
                .getApplication()
                .evaluateExpressionGet(context, "#{i18n}", PropertyResourceBundle.class);

        return bundle.getString(key);
    }
}