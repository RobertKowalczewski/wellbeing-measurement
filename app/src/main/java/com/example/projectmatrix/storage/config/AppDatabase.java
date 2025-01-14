package com.example.projectmatrix.storage.config;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.projectmatrix.storage.dao.model.matrix.MatrixData;
import com.example.projectmatrix.storage.dao.model.smartwatch.SmartwatchData;
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;
import com.example.projectmatrix.storage.dao.repository.matrix.MatrixDataRepository;
import com.example.projectmatrix.storage.dao.repository.smartwatch.SmartwatchDataRepository;
import com.example.projectmatrix.storage.dao.repository.user.WellbeingUserRepository;
import com.example.projectmatrix.storage.utils.Converter;

@Database(entities = {WellbeingUser.class, MatrixData.class, SmartwatchData.class}, version = 7, exportSchema = false)
@TypeConverters(Converter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WellbeingUserRepository wellbeingUserRepository();
    public abstract MatrixDataRepository matrixDataRepository();
    public abstract SmartwatchDataRepository smartwatchDataRepository();
}
