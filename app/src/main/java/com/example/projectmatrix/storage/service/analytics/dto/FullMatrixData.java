package com.example.projectmatrix.storage.service.analytics.dto;

import androidx.room.ColumnInfo;

import java.util.Date;

import lombok.ToString;

@ToString
public class FullMatrixData {

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "surname")
    public String surname;

    @ColumnInfo(name = "phoneNumber")
    public String phoneNumber;

    @ColumnInfo(name = "realCoordinateX")
    public double realCoordinateX;

    @ColumnInfo(name = "realCoordinateY")
    public double realCoordinateY;

    @ColumnInfo(name = "matrixCoordinateX")
    public double matrixCoordinateX;

    @ColumnInfo(name = "matrixCoordinateY")
    public double matrixCoordinateY;

    @ColumnInfo(name = "modificationTimestamp")
    public Date timestamp;
}
