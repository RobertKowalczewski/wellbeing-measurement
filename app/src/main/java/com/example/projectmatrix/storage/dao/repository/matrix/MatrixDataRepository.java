package com.example.projectmatrix.storage.dao.repository.matrix;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.projectmatrix.storage.dao.model.matrix.MatrixData;
import com.example.projectmatrix.storage.dao.repository.AbstractRepository;

import java.util.List;
import java.util.Optional;

@Dao
public interface MatrixDataRepository extends AbstractRepository<MatrixData> {

    @Override
    @Query("SELECT * FROM matrixData")
    List<MatrixData> findAll();

    @Override
    @Query("SELECT * FROM matrixData WHERE id = :id")
    Optional<MatrixData> findById(long id);
}
