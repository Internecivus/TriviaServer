package com.trivia.admin.controller.user;

import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Message;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.service.UserService;
import com.trivia.persistence.entity.QuestionEntity;
import com.trivia.persistence.entity.UserEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.security.enterprise.SecurityContext;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Named
@Dependent
public class UserController {
    private @Inject UserService userService;
    private @Inject SecurityContext securityContext;
    private transient @Inject FacesContext facesContext;

    @PostConstruct
    public void init() {}

    public LazyDataModel<UserEntity> findAll() {
        LazyDataModel<UserEntity> lazyUsers;
        lazyUsers = new LazyDataModel<UserEntity>() {
            @Override
            public List<UserEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                String searchString = (filters.get("globalFilter") != null) ? filters.get("globalFilter").toString() : null;

                try {
                    List<UserEntity> result = userService.findAll(
                            first / pageSize + 1,
                            pageSize,
                            sortField,
                            com.trivia.core.utility.SortOrder.valueOf(sortOrder.toString()),
                            searchString
                    );
                    super.setRowCount(userService.getLastCount());
                    return result;
                }
                catch (BusinessException e) {
                    Message.addErrorGlobal(i18n.get("failure"), i18n.get("services.failure"));
                }

                return null;
            }

            @Override
            public Object getRowKey(UserEntity object) {
                return super.getRowKey(object);
            }
        };

        return lazyUsers;
    }

    public String create(UserEntity userEntity) {
        try {
            userService.create(userEntity);

            Message.addInfoGlobalFlash(i18n.get("success"), i18n.get("create.success"));
            return facesContext.getViewRoot().getViewId() + "?faces-redirect=true";
        }
        catch (EntityExistsException e) {
            Message.addErrorGlobal(i18n.get("failure"), i18n.get("error.exists.message"));
        }

        return null;
    }

    public void update(UserEntity userEntity) {
        try {
            userService.update(userEntity);

            Message.addInfoGlobal(i18n.get("success"), i18n.get("update.success"));
        }
        catch (BusinessException e) {
            Message.addErrorGlobal(i18n.get("failure"), i18n.get("update.failure"));
        }
    }

    public void delete(int id) {
        try {
            userService.deleteById(id); /// TODO: needs to update the dataTable
            Message.addInfoFor("growl", i18n.get("success"), i18n.get("delete.success"));
        }
        catch (NotAuthorizedException e) {
            Message.addErrorFor("growl", i18n.get("failure"), i18n.get("access.failure"));
        }
        catch (BusinessException e) {
            Message.addErrorFor("growl", i18n.get("failure"), i18n.get("delete.failure"));
        }
    }
}
