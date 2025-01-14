package com.example.projectmatrix.dto;

import lombok.Data;

@Data
public class SmartwatchDataDto {
    public double heartRate;

    public SmartwatchDataDto(double heartRate) {
        this.heartRate = heartRate;
    }
}
