package com.trivia.core.service;

import com.trivia.persistence.dto.client.CategoryClient;
import com.trivia.persistence.entity.Category;
import com.trivia.persistence.entity.RoleType;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

@Stateless
@RolesAllowed(RoleType.Name.PRINCIPAL)
public class CategoryService extends Service<Category> {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Resource SessionContext sessionContext;
    private @Inject Logger logger;

    public CategoryService() {}

    public Set<Category> getAll() {
        Set<Category> categoryList;
        TypedQuery<Category> query = em.createQuery("SELECT c from Category c", Category.class);

        categoryList = new HashSet<>(query.getResultList());

        return categoryList;
    }

    public Set<String> getAllNames() {
        Set<Category> categoryList = getAll();
        Set<String> namesList = new HashSet<>();

        for (Category category : categoryList) {
            namesList.add(category.getName());
        }
        return namesList;
    }

    public Collection<CategoryClient> toDto(Collection<Category> entities) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(entities, new TypeToken<List<CategoryClient>>() {}.getType());
    }

    @Override
    @RolesAllowed(RoleType.Name.MODERATOR)
    public void deleteById(Object id) {
        super.deleteById(id);
        logger.info("Category id: {} was DELETED by user: {}", id, sessionContext.getCallerPrincipal().getName());
    }

    @Override
    @RolesAllowed(RoleType.Name.CONTRIBUTOR)
    public void update(Category updatedEntity) {
        super.update(updatedEntity);
        logger.info("Category id: {} was UPDATED by user: {}", updatedEntity.getId(), sessionContext.getCallerPrincipal().getName());
    }
}
