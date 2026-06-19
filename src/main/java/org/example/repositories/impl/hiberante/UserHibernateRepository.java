//package org.example.repositories.impl.hiberante;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.example.models.User;
//import org.example.repositories.UserRepository;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//@Profile({"jpa", "adapter"})
//@Transactional
//public class UserHibernateRepository extends JpaRepository<User, String> {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public List<User> findAll() {
//        return entityManager.createQuery("FROM User", User.class).getResultList();
//    }
//
//    @Override
//    public Optional<User> findById(String id) {
//        return Optional.ofNullable(entityManager.find(User.class, id));
//    }
//
//    Optional<User> findByLogin(String login);
//
//    @Override
//    public User save(User user) {
//        return entityManager.merge(user);
//    }
//
//    @Override
//    public void deleteByLogin(String login) {
//        findByLogin(login).ifPresent(user -> deleteById(user.getId()));
//    }
//
//    @Override
//    public void deleteById(String id) {
//        User user = entityManager.find(User.class, id);
//        if (user != null) {
//            entityManager.remove(user);
//        }
//    }
//
//    @Override
//    public List<User> findByAddress() {
//        return entityManager.find(User.class, ).createQuery("FROM User", User.class).getResultList()
//    }
//}