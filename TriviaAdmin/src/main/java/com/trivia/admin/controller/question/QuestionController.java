package com.trivia.admin.controller.question;

import com.trivia.admin.resources.i18n;
import com.trivia.admin.utility.Message;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.service.CategoryService;
import com.trivia.core.service.QuestionService;
import com.trivia.persistence.entity.QuestionEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Named
@Dependent
//TODO: Put delete, find, create, etc. here??
public class QuestionController {
    private @Inject QuestionService questionService;
    private transient @Inject FacesContext facesContext;

    public LazyDataModel<QuestionEntity> findAll() {
        LazyDataModel<QuestionEntity> lazyQuestions;
        lazyQuestions = new LazyDataModel<QuestionEntity>() {
            @Override
            public List<QuestionEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                String searchString = (filters.get("globalFilter") != null) ? filters.get("globalFilter").toString() : null;

                try {
                    List<QuestionEntity> result = questionService.findAll(
                            first / pageSize + 1,
                            pageSize,
                            sortField,
                            com.trivia.core.utility.SortOrder.valueOf(sortOrder.toString()),
                            searchString
                    );
                    super.setRowCount(questionService.getLastCount());
                    return result;
                }
                catch (BusinessException e) {
                    Message.addErrorGlobal(i18n.get("failure"), i18n.get("services.failure"));
                }

                return null;
            }

            @Override
            public Object getRowKey(QuestionEntity object) {
                return super.getRowKey(object);
            }
        };

        return lazyQuestions;
    }

    public String create(QuestionEntity questionEntity, UploadedFile uploadedImage) {
        try {
            if (uploadedImage.getSize() > 0) {
                questionService.createWithImage(questionEntity, uploadedImage.getFileName(), uploadedImage.getInputstream());
            }
            else {
                questionService.create(questionEntity);
            }

            Message.addInfoGlobalFlash(i18n.get("success"), i18n.get("create.success"));
            return facesContext.getViewRoot().getViewId() + "?faces-redirect=true";
        }
        catch (IOException e) {
            Message.addErrorGlobal(i18n.get("failure"), i18n.get("create.failure"));
        }
        catch (EntityExistsException e) {
            Message.addErrorGlobal(i18n.get("failure"), i18n.get("error.exists.message"));
        }

        return null;
    }

    public void update(QuestionEntity questionEntity) {
        try {
            questionService.update(questionEntity);

            Message.addInfoGlobal(i18n.get("success"), i18n.get("update.success"));
        }
        catch (BusinessException e) {
            Message.addErrorGlobal(i18n.get("failure"), i18n.get("update.failure"));
        }
    }

    public void delete(int id) {
        try {
            questionService.deleteById(id); /// TODO: needs to update the dataTable
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
