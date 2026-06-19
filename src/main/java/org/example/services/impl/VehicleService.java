package org.example.services.impl;

import org.example.models.Vehicle;
import org.example.models.VehicleValidator;
import org.example.repositories.VehicleRepository;
import org.example.services.VehicleServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class VehicleService implements VehicleServiceInterface {
    private final VehicleRepository vehicleRepository;
    private final RentalHibernateService rentalService;
    private final VehicleValidator vehicleValidator;
    private final JsonSchemaValidationService jsonValidator;

    public VehicleService(VehicleRepository vehicleRepository, RentalHibernateService rentalService, VehicleValidator vehicleValidator, JsonSchemaValidationService jsonValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalService = rentalService;
        this.vehicleValidator = vehicleValidator;
        this.jsonValidator = jsonValidator;
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

        Set<String> errors = jsonValidator.validateAttributes(vehicle.getAttributes(), "category-schema.json");

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Błędne atrybuty JSON pojazdu! Powód: " + String.join(", ", errors));
        }

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