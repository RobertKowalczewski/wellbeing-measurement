package com.example.projectmatrix.storage.dao.model.matrix;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.example.projectmatrix.storage.dao.model.AbstractEntity;
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;

@Entity(foreignKeys = @ForeignKey(
        entity = WellbeingUser.class,
        parentColumns = "id",
        childColumns = "wellbeingUserId",
        onDelete = ForeignKey.CASCADE
))
public class MatrixData extends AbstractEntity {
    public long wellbeingUserId;
    public double realCoordinateX;
    public double realCoordinateY;
    public double matrixCoordinateX;
    public double matrixCoordinateY;
}
