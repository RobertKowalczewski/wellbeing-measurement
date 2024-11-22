package com.example.projectmatrix.storage.dao.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import lombok.Data;

@Entity
@Data
public class AbstractEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public Date creationTimestamp;

    public Date modificationTimestamp;
}
