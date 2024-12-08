package com.example.projectmatrix.storage.dao.model.smartwatch;

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
public class SmartwatchData extends AbstractEntity {
    public long wellbeingUserId;
    public double heartRate;
}
