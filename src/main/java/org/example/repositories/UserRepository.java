package org.example.repositories;

import org.example.models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByLogin(String login);
    User save(User user);
    void deleteByLogin(String login);
    void deleteById(String id);
}