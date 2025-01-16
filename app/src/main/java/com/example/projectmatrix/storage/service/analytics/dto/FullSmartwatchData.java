package com.example.projectmatrix.storage.service.analytics.dto;

import androidx.room.ColumnInfo;

import java.util.Date;

public class FullSmartwatchData {

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "surname")
    public String surname;

    @ColumnInfo(name = "phoneNumber")
    public String phoneNumber;

    @ColumnInfo(name = "heartRate")
    public double heartRate;

    @ColumnInfo(name = "modificationTimestamp")
    public Date timestamp;
}
