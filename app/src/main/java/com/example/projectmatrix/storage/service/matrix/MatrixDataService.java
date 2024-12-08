package com.example.projectmatrix.storage.service.matrix;

import com.example.projectmatrix.storage.dao.model.matrix.MatrixData;
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;
import com.example.projectmatrix.storage.dao.repository.matrix.MatrixDataRepository;
import com.example.projectmatrix.storage.service.AbstractService;

public class MatrixDataService extends AbstractService<MatrixData> {

    private final MatrixDataRepository matrixDataRepository;

    public MatrixDataService(MatrixDataRepository repository) {
        super(repository);

        this.matrixDataRepository = repository;
    }

    public MatrixData createFor(WellbeingUser wellbeingUser) {
        var matrixData = new MatrixData();
        matrixData.wellbeingUserId = wellbeingUser.id;
        return matrixData;
    }
}
