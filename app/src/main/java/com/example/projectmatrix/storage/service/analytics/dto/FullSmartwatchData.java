package com.example.projectmatrix.storage.service.analytics.dto;

import androidx.room.ColumnInfo;

import java.util.Date;

public class FullSmartwatchData {

    @ColumnInfo(name = "name")
    public long name;

    @ColumnInfo(name = "surname")
    public long surname;

    @ColumnInfo(name = "phoneNumber")
    public long phoneNumber;

    @ColumnInfo(name = "heartRate")
    public double heartRate;

    @ColumnInfo(name = "modificationTimestamp")
    public Date timestamp;
}
