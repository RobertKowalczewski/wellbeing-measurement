package com.example.projectmatrix.storage.dao.model.user;

import androidx.room.Entity;

import com.example.projectmatrix.storage.dao.model.AbstractEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString
public class WellbeingUser extends AbstractEntity {
    public String name;
    public String surname;
    public String phoneNumber;
}
