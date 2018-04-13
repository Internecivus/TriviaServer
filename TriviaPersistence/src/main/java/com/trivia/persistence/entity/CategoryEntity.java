package com.trivia.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
@Entity
@Table(name = "category", schema = "Trivia")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "id")
    private Integer id;

    @Basic
    @NotNull
    @Column(name = "name")
    private String name;

    @Basic
    @NotNull
    @Column(name = "description")
    private String description;

    @Basic
    @NotNull
    @Column(name = "image")
    private String image;
//
//    @ManyToMany(mappedBy = "categories", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<QuestionEntity> questions = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

//    public List<QuestionEntity> getQuestions() {
//        return questions;
//    }
//
//    public void setQuestions(List<QuestionEntity> questions) {
//        this.questions = questions;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryEntity that = (CategoryEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return image != null ? image.equals(that.image) : that.image == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }
}