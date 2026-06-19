package org.example.repositories;

import org.example.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}