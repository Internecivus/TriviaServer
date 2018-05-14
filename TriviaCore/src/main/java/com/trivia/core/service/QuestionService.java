package com.trivia.core.service;

import com.trivia.core.exception.InvalidInputException;
import com.trivia.core.exception.SystemException;
import com.trivia.core.utility.Generator;
import com.trivia.core.utility.ImageUtil;
import com.trivia.persistence.dto.client.QuestionClient;
import com.trivia.persistence.entity.*;
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
import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;



@Stateless
@RolesAllowed(RoleType.Name.PRINCIPAL)
public class QuestionService extends Service<Question> {
    @PersistenceContext(unitName = "TriviaDB")
    private EntityManager em;
    private @Inject UserService userService;
    private @Inject Logger logger;
    private @Resource SessionContext sessionContext;

    private final static Integer PAGE_SIZE_RANDOM_DEFAULT = 10;
    private final static Integer PAGE_SIZE_RANDOM_MAX = 20;

    public QuestionService() {
        super.DEFAULT_SORT_COLUMN = Question_.dateCreated;
        super.SEARCHABLE_COLUMNS = SORTABLE_COLUMNS = new HashSet<>(Arrays.asList(
            Question_.id, Question_.answerFirst, Question_.question, Question_.dateLastModified,
            Question_.answerSecond, Question_.answerThird, Question_.answerFourth,
            Question_.answerCorrect, Question_.comment, Question_.dateCreated
        ));
    }

    public List<Question> getRandomFromCategory(Integer size, String category) {
        if (category == null || category.trim().length() < 1) throw new InvalidInputException("No category specified.");
        if (size == null) size = PAGE_SIZE_RANDOM_DEFAULT;
        if (size < 1) throw new InvalidInputException(String.format("Size must be a positive number (is %d).", size));
        if (size > PAGE_SIZE_RANDOM_MAX) throw new InvalidInputException(String.format(
                    "Size is over the maximum allowed size that is %d (given %d).", PAGE_SIZE_RANDOM_MAX, size));

        List<Question> questions = new ArrayList<>();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Question> query = builder.createQuery(Question.class);
        Root<Question> root = query.from(Question.class);
        query.select(root);

        Join join = root.join(Question_.categories);
        builder.equal(join.get(Category_.name), category);
        TypedQuery<Question> typedQuery = em.createQuery(query);

        int count = typedQuery.getResultList().size();
        if (size > count) throw new InvalidInputException(String.format(
                    "Not enough questions matching category '%s' found (found only %d out of %d).", category, count, size));

        int[] randomUniqueArray = Generator.generateRandomUniqueArray(size, count);
        for (int i = 0; i < randomUniqueArray.length; i++) {
            typedQuery.setFirstResult(randomUniqueArray[i]).setMaxResults(1);
            questions.addAll(typedQuery.getResultList()); // This logic is unfortunate.
        }

        return questions;
    }

    @Override
    @RolesAllowed(RoleType.Name.CONTRIBUTOR)
    public void update(Question updatedQuestion) {
        super.update(updatedQuestion);
        logger.info("Question id: {} was UPDATED by user: {}", updatedQuestion.getId(), sessionContext.getCallerPrincipal().getName());
    }

    @Override
    @RolesAllowed(RoleType.Name.MODERATOR)
    public void deleteById(Object id) {
        super.deleteById(id);
        logger.info("Question id: {} was DELETED by user: {}", id, sessionContext.getCallerPrincipal().getName());
    }

    public void createWithImage(Question newQuestion, String fileName, InputStream inputStream) {
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

    @Override
    @RolesAllowed(RoleType.Name.ADMIN)
    public Question create(Question newQuestion) {
        User user = userService.findByField(User_.name, sessionContext.getCallerPrincipal().getName());
        newQuestion.setUser(user);

        super.create(newQuestion);
        logger.info("Question id: {} was CREATED by user id: {}", newQuestion.getId(), sessionContext.getCallerPrincipal().getName());
        return newQuestion;
    }

    public List<QuestionClient> toDto(List<Question> entities) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(entities, new TypeToken<List<QuestionClient>>() {}.getType());
    }
}