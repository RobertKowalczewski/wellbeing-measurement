package com.example.projectmatrix.storage.dao.repository.smartwatch;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.projectmatrix.storage.dao.model.smartwatch.SmartwatchData;
import com.example.projectmatrix.storage.dao.repository.AbstractRepository;

import java.util.List;
import java.util.Optional;

@Dao
public interface SmartwatchDataRepository extends AbstractRepository<SmartwatchData> {

    @Override
    @Query("SELECT * FROM smartwatchData")
    List<SmartwatchData> findAll();

    @Override
    @Query("SELECT * FROM smartwatchData WHERE id = :id")
    Optional<SmartwatchData> findById(long id);
}
