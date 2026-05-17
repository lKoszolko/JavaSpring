package org.example.services.servicesInterfaces;

import org.example.models.User;

import java.util.Optional;

public interface AuthServiceInterface {
    boolean register(String login, String rawPassword);
    Optional<User> login(String login, String rawPassword);
}
