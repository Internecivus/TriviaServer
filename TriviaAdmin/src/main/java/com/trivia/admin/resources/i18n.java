package com.trivia.admin.resources;

import com.trivia.admin.controller.LocaleController;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class i18n {
    public i18n() {}

    /**
     * It's unfortunate that IntelliJ does not autofetch the key-value pairs if we use this method.
     * If we want this, we can inject PropertyResourceBundle directly and use the getString() method.
     * */
    public static String get(String key, Object... parameters) {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        ResourceBundle bundle = PropertyResourceBundle.getBundle("i18n", locale);
        String message = key;
        if (bundle.containsKey(key)) {
            message = bundle.getString(message);
        }
        return new String(((parameters.length == 0) ? message : String.format(message, parameters)).getBytes(), StandardCharsets.UTF_8);
    }
}