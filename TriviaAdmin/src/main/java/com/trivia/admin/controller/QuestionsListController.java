package com.trivia.admin.controller;

import com.trivia.core.exception.BusinessException;
import com.trivia.core.services.CategoryBean;
import com.trivia.core.services.QuestionBean;
import com.trivia.persistence.entity.QuestionEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Named
@ViewScoped
public class QuestionsListController implements Serializable {
    @Inject private QuestionBean questionBean;
    @Inject private CategoryBean categoryBean;
    @Inject private transient FacesContext facesContext;
    @Inject private transient PropertyResourceBundle viewMessages;
    private LazyDataModel<QuestionEntity> questions;
    private String searchString;

    @PostConstruct
    public void init() {
        questions = new LazyDataModel<QuestionEntity>() {
            @Override
            public List<QuestionEntity> load(int pageFirst, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                String searchString = (filters.get("globalFilter") != null) ? filters.get("globalFilter").toString() : null;

                try {
                    List<QuestionEntity> result = questionBean.findAll(pageFirst / pageSize + 1, pageSize, sortField, com.trivia.core.services.SortOrder.valueOf(sortOrder.toString()), searchString);
                    questions.setRowCount(questionBean.getLastCount());

                    return result;
                }
                catch (BusinessException e) {
                    facesContext.addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, "Error", viewMessages.getString("database.failure")));
                }

                return null;
            }

            @Override
            public Object getRowKey(QuestionEntity object) {
                return super.getRowKey(object);
            }
        };
    }

    public void setQuestions(LazyDataModel<QuestionEntity> questions) {
        this.questions = questions;
    }

    public LazyDataModel<QuestionEntity> getQuestions() {
        return questions;
    }

    public void delete(int id) {
        questionBean.deleteById(id);
    }
}