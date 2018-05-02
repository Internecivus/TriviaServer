package com.trivia.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


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
    private Integer id;

    @Basic
    @NotBlank(message = "{field.required}")
    @Size(min = 6, max = 20, message = "{password.length}")
    @Column(name = "password")
    private String password;

    @Basic
    @NotBlank(message = "{field.required}")
    @Size(min = 5, max = 20, message = "{name.length}")
    @Column(name = "name")
    private String name;

    @Basic
    @NotBlank
    @Column(name = "provider_key")
    private String providerKey;

    @Basic
    @NotBlank
    @Column(name = "provider_secret")
    private String providerSecret;

    @Basic
    @NotNull
    @Column(name = "date_created")
    private Timestamp dateCreated;

    @NotEmpty
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_map",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, orphanRemoval = true)
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

    public String getProviderKey() {
        return providerKey;
    }

    public void setProviderKey(String providerKey) {
        this.providerKey = providerKey;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = new Timestamp(dateCreated.getTime());
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getProviderSecret() {
        return providerSecret;
    }

    public void setProviderSecret(String providerSecret) {
        this.providerSecret = providerSecret;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public Set<String> getRolesNames() {
        return getRoles().stream().map(g -> g.getName().name()).collect(Collectors.toSet());
    }

    public boolean hasRole(Role role) {
        return getRoles().stream().map(g -> g.getName().name()).collect(Collectors.toSet()).contains(role);
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