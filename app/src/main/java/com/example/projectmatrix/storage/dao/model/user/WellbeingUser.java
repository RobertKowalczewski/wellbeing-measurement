package com.example.projectmatrix.storage.dao.model.user;

import androidx.room.Entity;

import com.example.projectmatrix.storage.dao.model.AbstractEntity;

@Entity
public class WellbeingUser extends AbstractEntity {
    public String name;
    public String surname;
    public String phoneNumber;
}
