package com.example.projectmatrix.storage.service.analytics;

import com.example.projectmatrix.storage.dao.repository.user.WellbeingUserRepository;
import com.example.projectmatrix.storage.service.analytics.dto.FullMatrixData;
import com.example.projectmatrix.storage.service.analytics.dto.FullSmartwatchData;

import java.util.List;

public class AnalyticsService {

    private final WellbeingUserRepository wellbeingUserRepository;

    public AnalyticsService(WellbeingUserRepository  wellbeingUserRepository) {
        this.wellbeingUserRepository = wellbeingUserRepository;
    }

    public List<FullMatrixData> retrieveAllMatrixDataForWellbeingUserId(long userId) {
        return wellbeingUserRepository.getFullMatrixData(userId);
    }

    public List<FullSmartwatchData> retrieveAllSmartwatchDataForWellbeingUserId(long userId) {
        return wellbeingUserRepository.getFullSmartwatchData(userId);
    }
}
