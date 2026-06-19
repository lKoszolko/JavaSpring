package org.example.services.impl;

import org.example.models.Coordinates;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Component
public class VehicleGpsScheduler {

    private final VehicleRepository vehicleRepository;
    private final Random random = new Random();

    public VehicleGpsScheduler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void simulateGpsMovement() {
        List<Vehicle> rentedVehicles = vehicleRepository.findAll().stream()
                .filter(Vehicle::isRented)
                .toList();

        for (Vehicle vehicle : rentedVehicles) {
            Coordinates coords = vehicle.getCurrentLocation();

            if (coords == null || coords.getLatitude() == null) {
                coords = new Coordinates(52.2297, 21.0122);
            }

            double randomLatOffset = (random.nextDouble() - 0.5) * 0.005;
            double randomLonOffset = (random.nextDouble() - 0.5) * 0.005;

            coords.setLatitude(coords.getLatitude() + randomLatOffset);
            coords.setLongitude(coords.getLongitude() + randomLonOffset);

            vehicle.setCurrentLocation(coords);
            vehicleRepository.save(vehicle);
        }

        System.out.println("GPS Tracker: Zaktualizowano lokalizacje " + rentedVehicles.size() + " pojazdów w ruchu.");
    }
}