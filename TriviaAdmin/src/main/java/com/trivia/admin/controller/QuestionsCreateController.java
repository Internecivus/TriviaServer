package com.trivia.admin.controller;

import com.trivia.admin.utility.Messages;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.service.CategoryBean;
import com.trivia.core.service.QuestionBean;
import com.trivia.persistence.entity.CategoryEntity;
import com.trivia.persistence.entity.QuestionEntity;
import org.primefaces.model.UploadedFile;
import com.trivia.core.utility.ImageManager;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.PropertyResourceBundle;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
@Named
@ViewScoped
public class QuestionsCreateController implements Serializable {
    @Inject private QuestionBean questionBean; //TODO: Needs to be transient?
    @Inject private CategoryBean categoryBean;
    @Inject private transient PropertyResourceBundle viewMessages;
    @Inject private transient FacesContext facesContext;
    private QuestionEntity questionEntity;
    private UploadedFile uploadedImage;
    private List<CategoryEntity> categoriesAvailable;
    private List<CategoryEntity> categoriesUsed;

    @PostConstruct
    public void init() {
        this.questionEntity = new QuestionEntity();
        this.categoriesAvailable = categoryBean.getAll();
    }

    public String create() {
        try {
            if (uploadedImage.getSize() > 0) {
                questionBean.createWithImage(questionEntity, uploadedImage.getFileName(), uploadedImage.getInputstream());
            }
            else {
                questionBean.create(questionEntity);
            }

            Messages.addInfoGlobalFlash(viewMessages.getString("success"), viewMessages.getString("create.success"));
            return facesContext.getViewRoot().getViewId() + "?faces-redirect=true";
        }
        catch (IOException e) {
            Messages.addErrorGlobal(viewMessages.getString("failure"), viewMessages.getString("create.failure"));
        }
        catch (EntityExistsException e) {
            Messages.addErrorGlobal(viewMessages.getString("failure"), viewMessages.getString("error.exists.message"));
        }

        return null;
    }

    public void setQuestionEntity(QuestionEntity newQuestionEntity) {
        this.questionEntity = newQuestionEntity;
    }

    public QuestionEntity getQuestionEntity() {
        return questionEntity;
    }

    public UploadedFile getUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(UploadedFile uploadedImage) {
        this.uploadedImage = uploadedImage;
    }

    public List<CategoryEntity> getCategoriesAvailable() {
        return categoriesAvailable;
    }

    public void setCategoriesAvailable(List<CategoryEntity> categoriesAvailable) {
        this.categoriesAvailable = categoriesAvailable;
    }

    public List<CategoryEntity> getCategoriesUsed() {
        return categoriesUsed;
    }

    public void setCategoriesUsed(List<CategoryEntity> categoriesUsed) {
        this.categoriesUsed = categoriesUsed;
    }
}