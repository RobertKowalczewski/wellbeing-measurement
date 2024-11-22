package com.example.projectmatrix.storage.dao.repository.user;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;
import com.example.projectmatrix.storage.dao.repository.AbstractRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Dao
public interface WellbeingUserRepository extends AbstractRepository<WellbeingUser> {

    @Override
    @Query("SELECT * FROM wellbeingUser")
    List<WellbeingUser> findAll();

    @Override
    @Query("SELECT * FROM wellbeingUser WHERE id = :id")
    Optional<WellbeingUser> findById(long id);
}
