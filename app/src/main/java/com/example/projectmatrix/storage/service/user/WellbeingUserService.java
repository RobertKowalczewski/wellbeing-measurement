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

    public WellbeingUser findOrCreateUser(String name, String surname, String phone) {

        var user = wellbeingUserRepository.findByNameAndSurnameAndPhone(name, surname, phone);

        if (user.isEmpty()) {
            var newUser = new WellbeingUser();
            newUser.name = name;
            newUser.surname = surname;
            newUser.phoneNumber = phone;
            save(newUser);
            return newUser;
        }
        return user.get();
    }
}
