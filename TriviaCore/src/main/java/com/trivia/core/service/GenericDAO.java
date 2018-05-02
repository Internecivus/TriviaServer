package com.trivia.core.service;

import com.trivia.core.utility.SortOrder;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

public interface GenericDAO<T> {
    T findById(int id);
    T getById(int id);

    <V> T findByField(SingularAttribute<T, V> field, V value);
    <V> T getByField(SingularAttribute<T, V> field, V value);

    void update(T updatedEntity);
    void create(T newEntity);

    void deleteById(int id);

    List<T> findAll(int pageCurrent, int pageSize, String sortField, SortOrder sortOrder, String searchString);

    Long count();
}
