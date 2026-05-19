package org.example.repositories.impl.hiberante;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("hibernate")
@Transactional
public class VehicleHibernateRepository implements VehicleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Vehicle> findAll() {
        return entityManager
                .createQuery("FROM Vehicle", Vehicle.class)
                .getResultList();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(entityManager.find(Vehicle.class, id));
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        return entityManager.merge(vehicle);
    }

    @Override
    public void deleteById(String id) {
        Vehicle vehicle = entityManager.find(Vehicle.class, id);
        if (vehicle != null) {
            entityManager.remove(vehicle);
        }
    }
}