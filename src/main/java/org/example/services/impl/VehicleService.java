package org.example.services.impl;

import org.example.models.Vehicle;
import org.example.models.VehicleValidator;
import org.example.repositories.VehicleRepository;
import org.example.services.VehicleServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class VehicleService implements VehicleServiceInterface {
    private final VehicleRepository vehicleRepository;
    private final RentalHibernateService rentalService;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepository, RentalHibernateService rentalService, VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalService = rentalService;
        this.vehicleValidator = vehicleValidator;
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + id));
    }

    public boolean isVehicleRented(String id) {
        return rentalService.vehicleHasActiveRental(id);
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        return List.of();
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> !isVehicleRented(v.getId()))
                .collect(Collectors.toList());
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        if(vehicle.getId() == null || vehicle.getId().trim().isEmpty()){
            vehicle.setId(UUID.randomUUID().toString());
        }
        vehicleValidator.validate(vehicle);
        return vehicleRepository.save(vehicle);
    }

    public void removeVehicle(String vehicleId) {
        if (isVehicleRented(vehicleId)) {
            throw new IllegalStateException("Nie można usunąć pojazdu, który jest aktualnie wypożyczony.");
        }
        findById(vehicleId);
        vehicleRepository.deleteById(vehicleId);
    }
}