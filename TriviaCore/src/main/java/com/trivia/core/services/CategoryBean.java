package com.trivia.core.services;

import com.trivia.persistence.entity.CategoryEntity;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

// TODO: Create interface for all beans?

@Stateless
public class CategoryBean {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    //@PersistenceContext(properties={@PersistenceProperty(name="javax.persistence.sharedCache.mode", value="ALL")})
    @Inject private Logger logger;

    public List<CategoryEntity> getAll() {
        List<CategoryEntity> categoryList;
        TypedQuery<CategoryEntity> query = em.createQuery("SELECT c from CategoryEntity c", CategoryEntity.class);

        categoryList = query.getResultList();

        return categoryList;
    }

    public List<String> getAllNames() {
        List<CategoryEntity> categoryList = getAll();
        List<String> namesList = new ArrayList<>();

        for (CategoryEntity category : categoryList) {
            namesList.add(category.getName());
        }

        return namesList;
    }
}
