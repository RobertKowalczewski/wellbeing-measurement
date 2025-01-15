package com.example.projectmatrix.storage.service.matrix;

import com.example.projectmatrix.storage.dao.model.matrix.MatrixData;
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;
import com.example.projectmatrix.storage.dao.repository.user.WellbeingUserRepository;

public class MatrixDataFactory {

    private final MatrixDataService matrixDataService;
    private final WellbeingUserRepository wellbeingUserRepository;

    public MatrixDataFactory(MatrixDataService matrixDataService, WellbeingUserRepository wellbeingUserRepository) {
        this.matrixDataService = matrixDataService;
        this.wellbeingUserRepository = wellbeingUserRepository;
    }

    public MatrixData create(WellbeingUser wellbeingUser, double realX, double realY, double matrixX, double matrixY) {
        var matrixData = matrixDataService.createFor(
                wellbeingUserRepository
                        .findByNameAndSurnameAndPhone(wellbeingUser.name, wellbeingUser.surname, wellbeingUser.phoneNumber)
                        .orElseThrow(() -> new IllegalStateException("User with name " + wellbeingUser.name + " " + wellbeingUser.surname + " does not exist.")));
        matrixData.realCoordinateX = realX;
        matrixData.realCoordinateY = realY;
        matrixData.matrixCoordinateX = matrixX;
        matrixData.matrixCoordinateY = matrixY;
        return matrixData;
    }
}
