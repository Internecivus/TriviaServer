package com.trivia.persistence.entity;



import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.registry.infomodel.User;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
@Entity
@Table(name = "question", schema = "Trivia")
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "question")
    @NotBlank(message = "{field.required}")
    private String question;

    @Basic
    @Column(name = "answer_first")
    @NotBlank(message = "{field.required}")
    private String answerFirst;

    @Basic
    @Column(name = "answer_second")
    @NotBlank(message = "{field.required}")
    private String answerSecond;

    @Basic
    @Column(name = "answer_third")
    @NotBlank(message = "{field.required}")
    private String answerThird;

    @Basic
    @Column(name = "answer_fourth")
    @NotBlank(message = "{field.required}")
    private String answerFourth;

    @Basic
    @Column(name = "comment")
    private String comment;

    @Basic
    @NotNull
    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Basic
    @NotNull(message = "{field.required}")
    @Min(value = 1, message = "{answerCorrect.between}")
    @Max(value = 4, message = "{answerCorrect.between}")
    @Column(name = "answer_correct")
    private Integer answerCorrect;

    @Basic
    @Column(name = "date_last_modified")
    private Timestamp dateLastModified;

    @Basic
    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)// insertable nullable
    private UserEntity user;

    @NotEmpty
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="question_category_map",
            joinColumns = {@JoinColumn(name = "question_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id", referencedColumnName = "id")}
    )
    private List<CategoryEntity> categories = new ArrayList<>();

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity userEntity) {
        this.user = userEntity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerFirst() {
        return answerFirst;
    }

    public void setAnswerFirst(String answerFirst) {
        this.answerFirst = answerFirst;
    }

    public String getAnswerSecond() {
        return answerSecond;
    }

    public void setAnswerSecond(String answerSecond) {
        this.answerSecond = answerSecond;
    }

    public String getAnswerThird() {
        return answerThird;
    }

    public void setAnswerThird(String answerThird) {
        this.answerThird = answerThird;
    }

    public String getAnswerFourth() {
        return answerFourth;
    }

    public void setAnswerFourth(String answerFourth) {
        this.answerFourth = answerFourth;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = new Timestamp(dateCreated.getTime());
    }

    public Integer getAnswerCorrect() {
        return answerCorrect;
    }

    public void setAnswerCorrect(Integer answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

    public Date getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(Date dateLastModified) {
        this.dateLastModified = new Timestamp(dateLastModified.getTime());
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionEntity that = (QuestionEntity) o;

        if (id != that.id) return false;
        if (answerCorrect != that.answerCorrect) return false;
        if (question != null ? !question.equals(that.question) : that.question != null) return false;
        if (answerFirst != null ? !answerFirst.equals(that.answerFirst) : that.answerFirst != null) return false;
        if (answerSecond != null ? !answerSecond.equals(that.answerSecond) : that.answerSecond != null) return false;
        if (answerThird != null ? !answerThird.equals(that.answerThird) : that.answerThird != null) return false;
        if (answerFourth != null ? !answerFourth.equals(that.answerFourth) : that.answerFourth != null) return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (dateCreated != null ? !dateCreated.equals(that.dateCreated) : that.dateCreated != null) return false;
        if (dateLastModified != null ? !dateLastModified.equals(that.dateLastModified) : that.dateLastModified != null)
            return false;
        return image != null ? image.equals(that.image) : that.image == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (answerFirst != null ? answerFirst.hashCode() : 0);
        result = 31 * result + (answerSecond != null ? answerSecond.hashCode() : 0);
        result = 31 * result + (answerThird != null ? answerThird.hashCode() : 0);
        result = 31 * result + (answerFourth != null ? answerFourth.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + answerCorrect;
        result = 31 * result + (dateLastModified != null ? dateLastModified.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }
}