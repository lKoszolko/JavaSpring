package org.example.services;

import org.example.models.User;
import org.example.web.security.DTO;

import java.util.List;

public interface UserServiceInterface {

    List<User> findAllUsers();

    User findById(String id);

    void deleteUser(String id, String loggedUserId);

    void registerUser(DTO.RegisterRequest request);
}
