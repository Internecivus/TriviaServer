package com.trivia.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

/**
 * TODO: We are mixing logic in User since it is acting as both a "normal" user and a provider. A separate provider
 * table/entity should be made.
 */
@Entity
@Table(name = "user", schema = "Trivia")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @NotBlank
    @Column(name = "password")
    private String password;

    @Basic
    @NotBlank
    @Column(name = "name")
    private String name;

    @Basic
    @NotBlank
    @Column(name = "api_key")
    private String apiKey;

    @Basic
    @NotBlank
    @Column(name = "api_secret")
    private String apiSecret;

    @Basic
    @NotBlank
    @Column(name = "provider_secret")
    private String providerSecret;

    @NotNull
    @Column(name = "requests")
    private int requests;

    @Basic
    @NotNull
    @Column(name = "date_created")
    private Timestamp dateCreated;

    @NotEmpty
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role_map",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(mappedBy = "question", targetEntity = QuestionEntity.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<QuestionEntity> questions = new ArrayList<>();

    public List<QuestionEntity> getQuestions() {
        return questions;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public String getProviderSecret() {
        return providerSecret;
    }

    public void setProviderSecret(String providerSecret) {
        this.providerSecret = providerSecret;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public Set<String> getRolesNames() {
        return getRoles().stream().map(g -> g.getName().name()).collect(Collectors.toSet());
    }

    public void addRole(RoleEntity role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(RoleEntity role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != that.id) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = password != null ? password.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}