package com.example.projectmatrix.storage.dao.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.example.projectmatrix.storage.dao.model.AbstractEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Dao
public interface AbstractRepository<T extends AbstractEntity> {

    List<T> findAll();

    Optional<T> findById(long id);

    @Insert
    default void insert(T entity) {
        entity.creationTimestamp = new Date();
        entity.modificationTimestamp = new Date();
        insertInternal(entity);
    }

    @Insert
    void insertInternal(T entity);

    @Update
    default void update(T entity) {
        entity.modificationTimestamp = new Date();
        updateInternal(entity);
    }

    @Update
    void updateInternal(T entity);

    @Delete
    void delete(T entity);
}
