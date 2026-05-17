package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.impl.hiberante.RentalHibernateRepository;
import org.example.repositories.impl.hiberante.UserHibernateRepository;
import org.example.repositories.impl.hiberante.VehicleHibernateRepository;
import org.example.services.servicesInterfaces.RentalServiceInterface;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class RentalHibernateService implements RentalServiceInterface {

    private final RentalHibernateRepository rentalRepo;
    private final VehicleHibernateRepository vehicleRepo;
    private final UserHibernateRepository userRepo;

    public RentalHibernateService(RentalHibernateRepository rentalRepo,
                                  VehicleHibernateRepository vehicleRepo,
                                  UserHibernateRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        if (userHasActiveRental(userId)) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie! Zwróć najpierw obecny pojazd.");
        }

        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Ten pojazd jest już wypożyczony przez kogoś innego.");
        }

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym id"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym id"));

        Rental rental = new Rental(
                UUID.randomUUID().toString(),
                vehicle,
                user,
                LocalDateTime.now().toString(),
                null
        );

        return rentalRepo.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie wypożyczonego pojazdu"));

        rental.setReturnDateTime(LocalDateTime.now().toString());
        return rentalRepo.save(rental);
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUser().getId()) && r.getReturnDateTime() == null)
                .findFirst();
    }

    @Override
    public List<Rental> findAllRentals() {
        return rentalRepo.findAll();
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUser().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepo.findAll().stream()
                .anyMatch(r -> r.getVehicle().getId().equals(vehicleId) && r.getReturnDateTime() == null);
    }
}