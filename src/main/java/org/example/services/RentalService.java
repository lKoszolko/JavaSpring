package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.servicesInterfaces.RentalServiceInterface;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class RentalService implements RentalServiceInterface {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findAll().stream()
                .anyMatch(r -> r.getVehicle().getId().equals(vehicleId) && r.getReturnDateTime() == null);
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(userId) && r.getReturnDateTime() == null)
                .findFirst();
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getReturnDateTime() != null)
                .collect(Collectors.toList());
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Taki pojazd nie istnieje."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Taki użytkownik nie istnieje."));

        if (findActiveRentalByUserId(userId).isPresent()) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie! Zwróć najpierw obecny pojazd.");
        }
        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Ten pojazd jest już wypożyczony przez kogoś innego.");
        }

        Rental rental = new Rental(UUID.randomUUID().toString(), vehicle, user, String.valueOf(LocalDateTime.now()), null);
        return rentalRepository.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental activeRental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie żadnego wypożyczonego pojazdu."));

        activeRental.setReturnDateTime(String.valueOf(LocalDateTime.now()));
        return rentalRepository.save(activeRental);
    }
}