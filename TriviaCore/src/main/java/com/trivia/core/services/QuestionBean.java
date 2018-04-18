package com.trivia.core.services;

import com.trivia.core.exception.EntityNotFoundException;
import com.trivia.core.utility.Generator;
import com.trivia.persistence.entity.CategoryEntity_;
import com.trivia.persistence.entity.QuestionEntity;
import com.trivia.persistence.entity.QuestionEntity_;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */


// TODO: Needs complete refactoring to use a single, more systematic JPA API (JPQL / Criteria API?)
// TODO: Needs heavy performance testing
@Stateless
public class QuestionBean {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    @EJB private UserBean userBean;
    @Inject private Logger logger;
    private final static Integer PAGE_SIZE_DEFAULT = 20;
    private final static Integer PAGE_SIZE_MAX = 100;
    private final static Integer PAGE_SIZE_RANDOM_DEFAULT = 20;
    private final static Integer PAGE_SIZE_RANDOM_MAX = 20;

    public QuestionEntity findById(int id) {
        QuestionEntity questionEntity = em.find(QuestionEntity.class, id);

        if (questionEntity == null) {
            throw new EntityNotFoundException();
        }

        return questionEntity;
    }

    public List<QuestionEntity> getRandomSizeFromCategory(int size, String category) {
        List<QuestionEntity> questions = new ArrayList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<QuestionEntity> query = builder.createQuery(QuestionEntity.class);
        Root<QuestionEntity> root = query.from(QuestionEntity.class);
        query.select(root);

        Join join = root.join(QuestionEntity_.categories);
        builder.equal(join.get(CategoryEntity_.name), category);
        TypedQuery<QuestionEntity> typedQuery = em.createQuery(query);

        int count = typedQuery.getResultList().size();
        size = (size > count) ? count : size;
        size = (size >= 0 && size <= PAGE_SIZE_RANDOM_MAX) ? size : PAGE_SIZE_RANDOM_DEFAULT;

        int[] randomUniqueArray = Generator.generateRandomUniqueArray(size, count);
        for (int i = 0; i < randomUniqueArray.length; i++) {
            typedQuery.setFirstResult(randomUniqueArray[i]).setMaxResults(1);
            questions.addAll(typedQuery.getResultList()); // This logic is unfortunate.
        }

        return questions;
    }

    //TODO: This selects EVERYTHING. Create another, more discriminating method for clients.
    public List<QuestionEntity> findAll(int pageCurrent, int pageSize, String sortField, SortOrder sortOrder, String searchString) {
        List<QuestionEntity> questions;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<QuestionEntity> query = builder.createQuery(QuestionEntity.class);
        Root<QuestionEntity> root = query.from(QuestionEntity.class);
        query.select(root);
        Path<?> path = getPath(sortField, root);


        if (searchString != null && searchString.trim().length() > 0) {
            Predicate filterCondition = builder.disjunction();
            filterCondition = builder.or(filterCondition, builder.like(root.get(QuestionEntity_.question), "%" + searchString + "%"));
            filterCondition = builder.or(filterCondition, builder.like(root.get(QuestionEntity_.answerFirst), "%" + searchString + "%"));
            filterCondition = builder.or(filterCondition, builder.like(root.get(QuestionEntity_.answerSecond), "%" + searchString + "%"));
            filterCondition = builder.or(filterCondition, builder.like(root.get(QuestionEntity_.answerThird), "%" + searchString + "%"));
            filterCondition = builder.or(filterCondition, builder.like(root.get(QuestionEntity_.answerFourth), "%" + searchString + "%"));
            filterCondition = builder.or(filterCondition, builder.like(root.get(QuestionEntity_.comment), "%" + searchString + "%"));
            query.where(filterCondition);
        }

        switch(sortOrder) {
            case ASCENDING:
                query.orderBy(builder.asc(path));
                break;
            case DESCENDING:
                query.orderBy(builder.desc(path));
                break;
            case UNSORTED:
                query.orderBy(builder.desc(path));
                break;
        }

        TypedQuery<QuestionEntity> typedQuery = em.createQuery(query);

        //TODO: TEMPORARY - read below
        lastCount = typedQuery.getResultList().size();

        pageSize = (pageSize >= 0 && pageSize <= PAGE_SIZE_MAX) ? pageSize : PAGE_SIZE_DEFAULT;
        typedQuery.setMaxResults(pageSize);
        pageCurrent = (pageCurrent >= 0) ? pageCurrent : 0;
        typedQuery.setFirstResult(pageCurrent * pageSize - pageSize);

        questions = typedQuery.getResultList();

        logger.info("Hello!");

        return questions;
    }

    public Long count() {
        Long count;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<QuestionEntity> root = query.from(QuestionEntity.class);
        query.select(builder.count(root));
        TypedQuery<Long> typedQuery = em.createQuery(query);
        count = typedQuery.getSingleResult();

        return count;
    }

    // TODO: Return QuestionEntity??
    public void update(QuestionEntity updatedQuestion) {
        QuestionEntity question = findById(updatedQuestion.getId());
        if (question != null) {
            updatedQuestion.setDateLastModified(new Timestamp(System.currentTimeMillis()));
            em.merge(updatedQuestion);
            em.flush();
        }
    }

    public void deleteById(int id) {
        QuestionEntity question = findById(id);
        em.remove(question);
        em.flush();
    }

    public void create(QuestionEntity newQuestion) {
        newQuestion.setDateCreated(new Timestamp(System.currentTimeMillis()));
        newQuestion.setUser(userBean.findById(1));

        em.persist(newQuestion);
        em.flush();
    }

    private Path<?> getPath(String field, Root<QuestionEntity> root) {
        Path<?> path;
        if (field == null) {
            path = root.get(QuestionEntity_.dateCreated);
        }
        else {
            switch (field) {
                case "id":
                    path = root.get(QuestionEntity_.id);
                    break;
                case "question":
                    path = root.get(QuestionEntity_.question);
                    break;
                case "answerFirst":
                    path = root.get(QuestionEntity_.answerFirst);
                    break;
                case "answerSecond":
                    path = root.get(QuestionEntity_.answerSecond);
                    break;
                case "answerThird":
                    path = root.get(QuestionEntity_.answerThird);
                    break;
                case "answerFourth":
                    path = root.get(QuestionEntity_.answerFourth);
                    break;
                case "comment":
                    path = root.get(QuestionEntity_.comment);
                    break;
                case "dateCreated":
                    path = root.get(QuestionEntity_.dateCreated);
                    break;
                case "dateUpdated":
                    path = root.get(QuestionEntity_.dateLastModified);
                    break;
                case "answerCorrect":
                    path = root.get(QuestionEntity_.answerCorrect);
                    break;
                default:
                    path = root.get(QuestionEntity_.dateCreated);
                    break;
            }
        }
        return path;
    }

    //TODO: TEMPORARY - This is crazy. Create data structure (EntityPage -- has page out of x, randomized, sort field, sort order, count, etc.) to fix this???
    private int lastCount; public int getLastCount() {return lastCount;} public void setLastCount(int lastCount) { this.lastCount = lastCount; }
}