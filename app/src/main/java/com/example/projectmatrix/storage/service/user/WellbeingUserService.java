package com.example.projectmatrix.storage.service.user;

import com.example.projectmatrix.storage.dao.model.user.WellbeingUser;
import com.example.projectmatrix.storage.dao.repository.user.WellbeingUserRepository;
import com.example.projectmatrix.storage.service.AbstractService;

public class WellbeingUserService extends AbstractService<WellbeingUser> {

    private final WellbeingUserRepository wellbeingUserRepository;


    public WellbeingUserService(WellbeingUserRepository repository) {
        super(repository);

        this.wellbeingUserRepository = repository;
    }
}
