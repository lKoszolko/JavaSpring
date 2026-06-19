package org.example.repositories.impl.adapter;

import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("adapter")
public class VehicleRepositoryAdapter {
    private final VehicleJpaDelegate delegate;

    public VehicleRepositoryAdapter(VehicleJpaDelegate delegate) {
        this.delegate = delegate;
    }

    public List<Vehicle> findAll() {
        return delegate.findAll();
    }

    public Optional<Vehicle> findById(String id) {
        return delegate.findById(id);
    }

    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        return delegate.save(vehicle);
    }

    public void deleteById(String id) {
        delegate.deleteById(id);
    }
}
