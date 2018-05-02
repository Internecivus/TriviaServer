package com.trivia.core.service;

import com.trivia.core.exception.EntityNotFoundException;
import com.trivia.core.exception.NotAuthorizedException;
import com.trivia.core.exception.SystemException;
import com.trivia.core.utility.Generator;
import com.trivia.core.utility.ImageUtil;
import com.trivia.core.utility.SortOrder;
import com.trivia.persistence.dto.client.QuestionClient;
import com.trivia.persistence.entity.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Stateless
public class QuestionService extends Service<QuestionEntity> {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @EJB UserService userService;
    private @Inject Logger logger;
    private @Resource SessionContext sessionContext;

    private final static Integer PAGE_SIZE_RANDOM_DEFAULT = 20;
    private final static Integer PAGE_SIZE_RANDOM_MAX = 20;

    public QuestionService() {
        super.PAGE_SIZE_DEFAULT = 100;
        super.PAGE_SIZE_MAX = 20;
        super.DEFAULT_SORT_COLUMN = QuestionEntity_.dateCreated;
        super.SEARCHABLE_COLUMNS = SORTABLE_COLUMNS = new ArrayList<>(Arrays.asList(
                QuestionEntity_.id, QuestionEntity_.answerFirst, QuestionEntity_.question, QuestionEntity_.dateLastModified,
                QuestionEntity_.answerSecond, QuestionEntity_.answerThird, QuestionEntity_.answerFourth,
                QuestionEntity_.answerCorrect, QuestionEntity_.comment, QuestionEntity_.dateCreated
        ));
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
        ModelMapper mapper = new ModelMapper();
        mapper.map(questions, questionsClient);

        return questionsClient;
    }

    public void update(QuestionEntity updatedQuestion) {
        UserEntity user = userService.findByField(UserEntity_.name, sessionContext.getCallerPrincipal().getName());
        updatedQuestion.setDateLastModified(new Timestamp(System.currentTimeMillis()));

        super.update(updatedQuestion);
        logger.info("Question id: {} UPDATED by user id: {}", updatedQuestion.getId(), user.getId());
    }

    //TODO: use @RolesAllowed("ADMIN") and @DeclareRoles({"ADMIN", "PROVIDER", etc}) for authorization
    public void deleteById(int id) {
        if (!sessionContext.isCallerInRole(Role.ADMIN.toString())) throw new NotAuthorizedException();
        UserEntity user = userService.findByField(UserEntity_.name, sessionContext.getCallerPrincipal().getName());

        super.deleteById(id);
        logger.info("Question id: {} DELETED by user id: {}", id, user.getId());
    }

    public void createWithImage(QuestionEntity newQuestion, String fileName, InputStream inputStream) {
        try {
            java.nio.file.Path imagePath = ImageUtil.saveImageAndGetPath(fileName, inputStream);
            String imageFileName = imagePath.getFileName().toString();
            newQuestion.setImage(imageFileName);
        }
        catch (IOException e) {
            throw new SystemException(e);
        }

        create(newQuestion);
    }

    public void create(QuestionEntity newQuestion) {
        newQuestion.setDateCreated(new Timestamp(System.currentTimeMillis()));
        UserEntity user = userService.findByField(UserEntity_.name, sessionContext.getCallerPrincipal().getName());
        newQuestion.setUser(user);

        super.create(newQuestion);
        logger.info("Question id: {} CREATED by user id: {}", newQuestion.getId(), user.getId());
    }
}