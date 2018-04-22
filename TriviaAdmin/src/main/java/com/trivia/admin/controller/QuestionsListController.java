package com.trivia.admin.controller;

import com.trivia.admin.utility.Messages;
import com.trivia.core.exception.BusinessException;
import com.trivia.core.service.CategoryBean;
import com.trivia.core.service.QuestionBean;
import com.trivia.persistence.entity.QuestionEntity;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
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
    private LazyDataModel<QuestionEntity> lazyQuestions;
    private String searchString;

    @PostConstruct
    public void init() {
        lazyQuestions = new LazyDataModel<QuestionEntity>() {
            @Override
            public List<QuestionEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                String searchString = (filters.get("globalFilter") != null) ? filters.get("globalFilter").toString() : null;

                try {
                    List<QuestionEntity> result = questionBean.findAll(
                            first / pageSize + 1,
                            pageSize,
                            sortField,
                            com.trivia.core.service.SortOrder.valueOf(sortOrder.toString()),
                            searchString
                    );
                    lazyQuestions.setRowCount(questionBean.getLastCount());

                    return result;
                }
                catch (BusinessException e) {
                    Messages.addErrorGlobal(viewMessages.getString("failure"), viewMessages.getString("services.failure"));
                }

                return null;
            }

            @Override
            public Object getRowKey(QuestionEntity object) {
                return super.getRowKey(object);
            }
        };
    }

    public void setLazyQuestions(LazyDataModel<QuestionEntity> lazyQuestions) {
        this.lazyQuestions = lazyQuestions;
    }

    public LazyDataModel<QuestionEntity> getLazyQuestions() {
        return lazyQuestions;
    }

    public void delete(int id) {
        try {
            questionBean.deleteById(id); /// TODO: needs to update the dataTable
            Messages.addInfoFor("growl", viewMessages.getString("success"), viewMessages.getString("delete.success"));
        }
        catch (BusinessException e) {
            Messages.addErrorFor("growl", viewMessages.getString("failure"), viewMessages.getString("delete.failure"));
        }
    }
}