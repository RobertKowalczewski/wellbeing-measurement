package com.example.projectmatrix.storage.service.matrix;

import com.example.projectmatrix.storage.dao.model.matrix.MatrixData;
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;

public class MatrixDataFactory {

    private final MatrixDataService matrixDataService;

    public MatrixDataFactory(MatrixDataService matrixDataService) {
        this.matrixDataService = matrixDataService;
    }

    public MatrixData create(WellbeingUser wellbeingUser, double realX, double realY, double matrixX, double matrixY) {
        var matrixData = matrixDataService.createFor(wellbeingUser);
        matrixData.realCoordinateX = realX;
        matrixData.realCoordinateY = realY;
        matrixData.matrixCoordinateX = matrixX;
        matrixData.matrixCoordinateY = matrixY;
        return matrixData;
    }
}
