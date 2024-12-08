package com.example.projectmatrix.storage.service;

import com.example.projectmatrix.storage.dao.model.AbstractEntity;
import com.example.projectmatrix.storage.dao.repository.AbstractRepository;

import java.util.List;
import java.util.Optional;

public abstract class AbstractService<T extends AbstractEntity> {

    protected final AbstractRepository<T> repository;

    public AbstractService(AbstractRepository<T> repository) {
        this.repository = repository;
    }

    public void save(T entity) {

        repository.findById(entity.id)
                .ifPresentOrElse(
                        repository::update,
                        () -> repository.insert(entity));
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public Optional<T> findById(long id) {
        return repository.findById(id);
    }
}
