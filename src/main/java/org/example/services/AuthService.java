package org.example.services;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.example.services.servicesInterfaces.AuthServiceInterface;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthService implements AuthServiceInterface {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(user -> BCrypt.checkpw(password, user.getPassword()));
    }

    public boolean register(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            return false;
        }
        Role assignedRole = login.equalsIgnoreCase("admin") ? Role.ADMIN : Role.USER;
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(UUID.randomUUID().toString(),login, hashedPassword, Role.USER);
        userRepository.save(newUser);
        return true;
    }
}