package org.example.repositories.impl.adapter;

import org.example.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleJpaDelegate extends JpaRepository<Vehicle, String> {
}
