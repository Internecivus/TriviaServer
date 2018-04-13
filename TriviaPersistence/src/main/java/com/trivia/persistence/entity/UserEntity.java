package com.trivia.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
//TODO: Use UUID or name for primary key instead of AI.
//TODO: There should be a different database/table for sensitive information.
//TODO: Name/email should be hashed too.
@Entity
@Table(name = "user", schema = "Trivia", catalog = "")
public class UserEntity implements Serializable {
    private String password;
    private int id;
    private String name;
    private RoleEntity role;

    //(fetch = FetchType.EAGER, mappedBy = "topic", cascade = CascadeType.ALL)

    @Basic @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id", nullable = false, insertable = false)
    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }
}