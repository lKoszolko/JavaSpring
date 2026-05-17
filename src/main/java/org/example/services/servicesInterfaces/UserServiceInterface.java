package org.example.services.servicesInterfaces;

import org.example.models.User;

import java.util.List;

public interface UserServiceInterface {

    List<User> findAllUsers();

    User findById(String id);

    void deleteUser(String id, String loggedUserId);
}
