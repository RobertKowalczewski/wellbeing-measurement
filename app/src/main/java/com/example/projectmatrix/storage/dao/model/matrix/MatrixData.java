package com.example.projectmatrix.storage.dao.model.matrix;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.example.projectmatrix.storage.dao.model.AbstractEntity;
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity(
    indices = {
            @Index("wellbeingUserId")
    },
    foreignKeys = @ForeignKey(
        entity = WellbeingUser.class,
        parentColumns = "id",
        childColumns = "wellbeingUserId",
        onDelete = ForeignKey.CASCADE
    )
)
@Data
@ToString
public class MatrixData extends AbstractEntity {
    public long wellbeingUserId;
    public double realCoordinateX;
    public double realCoordinateY;
    public double matrixCoordinateX;
    public double matrixCoordinateY;
}
