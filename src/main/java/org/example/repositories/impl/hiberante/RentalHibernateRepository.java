//package org.example.repositories.impl.hiberante;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.example.models.Rental;
//import org.example.repositories.RentalRepository;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//@Profile({"jpa", "adapter"})
//@Transactional
//public class RentalHibernateRepository implements RentalRepository {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public Rental save(Rental rental) {
//        return entityManager.merge(rental);
//    }
//
//    @Override
//    public Optional<Rental> findById(String id) {
//        return Optional.ofNullable(entityManager.find(Rental.class, id));
//    }
//
//    @Override
//    public List<Rental> findAll() {
//        return entityManager.createQuery("FROM Rental", Rental.class).getResultList();
//    }
//
//    @Override
//    public void deleteById(String id) {
//        Rental rental = entityManager.find(Rental.class, id);
//        if (rental != null) {
//            entityManager.remove(rental);
//        }
//    }
//
//    @Override
//    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
//        var query = entityManager.createQuery("""
//                FROM Rental r
//                WHERE r.vehicle.id = :vehicleId
//                AND r.returnDateTime IS NULL
//                """, Rental.class);
//        query.setParameter("vehicleId", vehicleId);
//        return query.getResultStream().findFirst();
//    }
//}