package com.trivia.admin.controller;

import com.trivia.core.services.CategoryBean;
import com.trivia.core.services.QuestionBean;
import com.trivia.persistence.entity.CategoryEntity;
import com.trivia.persistence.entity.QuestionEntity;
import org.primefaces.model.UploadedFile;
import com.trivia.core.resources.ImageManager;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
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
    @Inject private QuestionBean questionBean;
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

        try {
            this.categoriesAvailable = categoryBean.getAll();
        }
        catch (Exception e) {
            // TODO: redirect or something?
        }
    }

    public String create() {
        try {
            if (uploadedImage != null) {
                InputStream imageStream = uploadedImage.getInputstream();
                Path imagePath = ImageManager.saveImageAndGetPath(imageStream);
                String imageFileName = imagePath.getFileName().toString();
                questionEntity.setImage(imageFileName);
            }
            //questionEntity.setCategories(categoriesUsed);
            questionBean.create(questionEntity);
            facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Success", viewMessages.getString("create.success")
            ));

            UIViewRoot view = facesContext.getViewRoot();
            facesContext.getExternalContext().getFlash().setKeepMessages(true);
            return view.getViewId() + "?faces-redirect=true";
        }
        catch (IOException e) {
            facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Failure", viewMessages.getString("create.failure")
            ));
        }
        catch (EntityExistsException e) {
            facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Failure", "HEEEEEHOOOO!"
            ));
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