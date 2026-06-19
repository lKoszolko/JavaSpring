package org.example.repositories;

import org.example.models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"jpa", "adapter"})
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(String login);
    List<User> findByAddress_City(String city);
}