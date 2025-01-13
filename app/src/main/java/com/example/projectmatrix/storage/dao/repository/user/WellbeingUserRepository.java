package com.example.projectmatrix.storage.dao.repository.user;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;
import com.example.projectmatrix.storage.dao.repository.AbstractRepository;

import java.util.List;
import java.util.Optional;

@Dao
public interface WellbeingUserRepository extends AbstractRepository<WellbeingUser> {

    @Override
    @Query("SELECT * FROM wellbeingUser")
    List<WellbeingUser> findAll();

    @Override
    @Query("SELECT * FROM wellbeingUser WHERE id = :id")
    Optional<WellbeingUser> findById(long id);

    @Query("SELECT * FROM wellbeingUser WHERE name = :name AND surname = :surname AND phoneNumber = :phone")
    Optional<WellbeingUser> findByNameAndSurnameAndPhone(String name, String surname, String phone);

    @Query("SELECT * FROM wellbeinguser u LEFT JOIN matrixData m ON u.id = m.wellbeingUserId LEFT JOIN smartwatchData s ON u.id = s.wellbeingUserId")
    List<String[]> getFullData(long wellbeingUserId);
}
