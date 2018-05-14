package com.trivia.admin.controller;

import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Messages;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.exception.NotAuthorizedException;

abstract class CrudController {
    protected void findAll() {

    }

    protected void delete(int id) {
        try {

            Messages.addInfoFor("growl", i18n.get("success"), i18n.get("delete.success"));
        }
        catch (NotAuthorizedException e) {
            Messages.addErrorFor("growl", i18n.get("failure"), i18n.get("access.failure"));
        }
        catch (BusinessException e) {
            Messages.addErrorFor("growl", i18n.get("failure"), i18n.get("delete.failure"));
        }
    }
}
