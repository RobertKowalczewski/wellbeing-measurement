package com.example.projectmatrix.storage.service.analytics;

import com.example.projectmatrix.storage.dao.repository.user.WellbeingUserRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnalyticsService {

    private final WellbeingUserRepository wellbeingUserRepository;

    public List<String[]> retrieveAllDataForWellbeingUserId(long userId) {
        return wellbeingUserRepository.getFullData(userId);
    }
}
