package org.example.repositories.impl.hiberante;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"jpa", "adapter"})
@Transactional
public class UserHibernateRepository implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("FROM User", User.class).getResultList();
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        var query = entityManager.createQuery("FROM User WHERE login = :login", User.class);
        query.setParameter("login", login);
        return query.getResultStream().findFirst();
    }

    @Override
    public User save(User user) {
        return entityManager.merge(user);
    }

    @Override
    public void deleteByLogin(String login) {
        findByLogin(login).ifPresent(user -> deleteById(user.getId()));
    }

    @Override
    public void deleteById(String id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}