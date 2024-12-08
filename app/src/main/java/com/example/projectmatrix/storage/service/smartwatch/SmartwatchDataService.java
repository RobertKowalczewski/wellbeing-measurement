package com.example.projectmatrix.storage.service.smartwatch;

import com.example.projectmatrix.storage.dao.model.smartwatch.SmartwatchData;
import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;
import com.example.projectmatrix.storage.dao.repository.smartwatch.SmartwatchDataRepository;
import com.example.projectmatrix.storage.service.AbstractService;

public class SmartwatchDataService extends AbstractService<SmartwatchData> {

    private final SmartwatchDataRepository smartwatchDataRepository;

    public SmartwatchDataService(SmartwatchDataRepository repository) {
        super(repository);

        this.smartwatchDataRepository = repository;
    }

    public SmartwatchData createFor(WellbeingUser wellbeingUser) {
        var smartwatchData = new SmartwatchData();
        smartwatchData.wellbeingUserId = wellbeingUser.id;
        return  smartwatchData;
    }
}
