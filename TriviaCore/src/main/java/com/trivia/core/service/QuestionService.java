package com.trivia.core.service;

import com.trivia.core.exception.EntityNotFoundException;
import com.trivia.core.exception.SystemException;
import com.trivia.core.utility.Generator;
import com.trivia.core.utility.ImageUtil;
import com.trivia.core.utility.SortOrder;
import com.trivia.persistence.dto.client.QuestionClient;
import com.trivia.persistence.entity.CategoryEntity_;
import com.trivia.persistence.entity.QuestionEntity;
import com.trivia.persistence.entity.QuestionEntity_;
import com.trivia.persistence.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.security.enterprise.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class QuestionService {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @EJB UserService userService;
    private @Inject Logger logger;
    private @Resource SessionContext securityContext;
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

    public List<QuestionClient> getRandomForClient(int size, String category) {
        List<QuestionClient> questionsClient = new ArrayList<>();
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

        //typedQuery.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(QuestionClient.class)

        ModelMapper mapper = new ModelMapper();
        mapper.map(questions, questionsClient);

        return questionsClient;
    }

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

    public void update(QuestionEntity updatedQuestion) {
        QuestionEntity question = findById(updatedQuestion.getId());
        if (question != null) {
            updatedQuestion.setDateLastModified(new Timestamp(System.currentTimeMillis()));
            em.merge(updatedQuestion);
            em.flush();
            logger.info("Question id: {} UPDATED by user id: {}", question.getId(), 5);
        }
    }

    public void deleteById(int id) {
        UserEntity user = userService.findByName(securityContext.getCallerPrincipal().getName());
        QuestionEntity question = findById(id);
        em.remove(question);
        em.flush();
        logger.info("Question id: {} DELETED by user id: {}", question.getId(), user.getId());
    }

    public void createWithImage(QuestionEntity questionEntity, String fileName, InputStream inputStream) {
        try {
            java.nio.file.Path imagePath = ImageUtil.saveImageAndGetPath(fileName, inputStream);
            String imageFileName = imagePath.getFileName().toString();
            questionEntity.setImage(imageFileName);
        }
        catch (IOException e) {
            throw new SystemException();
        }

        create(questionEntity);
    }

    public void create(QuestionEntity questionEntity) {
        questionEntity.setDateCreated(new Timestamp(System.currentTimeMillis()));
        UserEntity user = userService.findByName(securityContext.getCallerPrincipal().getName());
        questionEntity.setUser(user);

        em.persist(questionEntity);
        em.flush();
        logger.info("Question id: {} CREATED by user id: {}", questionEntity.getId(), user.getId());
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

    //TODO: TEMPORARY - This is crazy. Wrap the list inside a data structure (e.g. EntityPage -- has page out of x, randomized, sort field, sort order, count, etc.) to fix this.
    private int lastCount; public int getLastCount() {return lastCount;} public void setLastCount(int lastCount) { this.lastCount = lastCount; }
}