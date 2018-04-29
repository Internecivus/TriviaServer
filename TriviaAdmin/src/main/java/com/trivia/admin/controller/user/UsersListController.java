package com.trivia.admin.controller.user;

import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Message;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.service.CategoryService;
import com.trivia.core.service.QuestionService;
import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.QuestionEntity;
import com.trivia.persistence.entity.UserEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class UsersListController implements Serializable {
    private @Inject UserService userService;
    private @Inject SecurityContext securityContext;
    private transient @Inject UserController userController;
    private transient @Inject FacesContext facesContext;
    private LazyDataModel<UserEntity> lazyUsers;
    private String searchString;

    @PostConstruct
    public void init() {
        lazyUsers = userController.findAll();
    }

    public LazyDataModel<UserEntity> getLazyUsers() {
        return lazyUsers;
    }
}