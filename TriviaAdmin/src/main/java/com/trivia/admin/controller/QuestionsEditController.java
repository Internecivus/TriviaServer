package com.trivia.admin.controller;

import com.trivia.core.exception.BusinessException;
import com.trivia.core.services.CategoryBean;
import com.trivia.core.services.QuestionBean;
import com.trivia.persistence.entity.CategoryEntity;
import com.trivia.persistence.entity.QuestionEntity;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
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
    @Inject private QuestionBean questionBean;
    @Inject private CategoryBean categoryBean;
    @Inject private transient FacesContext facesContext;
    @Inject private transient PropertyResourceBundle viewMessages;
    private QuestionEntity questionEntity;
    private List<String> categoriesAvailable;
    private List<String> categoriesUsed;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        int id = Integer.valueOf(request.getParameter("id"));
        this.questionEntity = questionBean.findById(id);
        this.categoriesAvailable = categoryBean.getAllNames();
        List<CategoryEntity> coll = questionEntity.getCategories();
        categoriesUsed = new ArrayList<>();

        for (CategoryEntity category : coll) {
            categoriesUsed.add(category.getName());
        }
    }

    public void update() {
        try {
            questionBean.update(questionEntity);

            facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Success", viewMessages.getString("update.success")
            ));
        }
        catch (BusinessException e) {
            facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Failure", viewMessages.getString("update.failure")
            ));
        }
    }

    public QuestionEntity getQuestionEntity() {
        return questionEntity;
    }

    public void setQuestionEntity(QuestionEntity questionEntity) {
        this.questionEntity = questionEntity;
    }

    public List<String> getCategoriesAvailable() {
        return categoriesAvailable;
    }

    public void setCategoriesAvailable(List<String> categoriesAvailable) {
        this.categoriesAvailable = categoriesAvailable;
    }

    public List<String> getCategoriesUsed() {
        return categoriesUsed;
    }

    public void setCategoriesUsed(List<String> categoriesUsed) {
        this.categoriesUsed = categoriesUsed;
    }

}
