package com.trivia.admin.controller;

import com.trivia.admin.utility.Messages;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.service.CategoryService;
import com.trivia.core.service.QuestionService;
import com.trivia.persistence.entity.CategoryEntity;
import com.trivia.persistence.entity.QuestionEntity;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
@Named
@ViewScoped
public class QuestionsEditController implements Serializable {
    @Inject private QuestionService questionService;
    @Inject private CategoryService categoryService;
    @Inject private transient FacesContext facesContext;
    @Inject private transient PropertyResourceBundle viewMessages;
    private QuestionEntity questionEntity;
    private UploadedFile uploadedImage;
    private List<CategoryEntity> categoriesAvailable;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        int id = Integer.valueOf(request.getParameter("id"));
        this.questionEntity = questionService.findById(id);
        this.categoriesAvailable = categoryService.getAll();
    }

    public void update() {
        try {
            questionService.update(questionEntity);

            Messages.addInfoGlobal(viewMessages.getString("success"), viewMessages.getString("update.success"));
        }
        catch (BusinessException e) {
            Messages.addErrorGlobal(viewMessages.getString("failure"), viewMessages.getString("update.failure"));
        }
    }

    public QuestionEntity getQuestionEntity() {
        return questionEntity;
    }

    public void setQuestionEntity(QuestionEntity questionEntity) {
        this.questionEntity = questionEntity;
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

}
