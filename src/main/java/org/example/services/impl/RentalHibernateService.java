package org.example.services.impl;

import com.stripe.exception.StripeException;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
// ZMIANA NR 1: Importujemy interfejsy, a nie konkretne implementacje!
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.PaymentServiceInterface;
import org.example.services.RentalServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class RentalHibernateService implements RentalServiceInterface {
    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final PaymentServiceInterface paymentService;
    private final LocationValidationService locationValidationService;

    public RentalHibernateService(RentalRepository rentalRepo,
                                  VehicleRepository vehicleRepo,
                                  UserRepository userRepo, PaymentService paymentService, LocationValidationService locationValidationService) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.paymentService = paymentService;
        this.locationValidationService = locationValidationService;
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
                LocalDateTime.now(),
                null
        );
        rental.getVehicle().setRented(true);
        return rentalRepo.save(rental);
    }

    @Override
    public String returnVehicle(String userId) throws StripeException {
        Rental rental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie wypożyczonego pojazdu"));

        if (!locationValidationService.isVehicleInAllowedZone(rental.getVehicle().getCurrentLocation())) {
            throw new IllegalStateException("Nie można zwrócić pojazdu! Pojazd znajduje się poza dozwoloną strefą zwrotu.");
        }

        rental.setReturnDateTime(LocalDateTime.now());

        Vehicle vehicle = rental.getVehicle();
        vehicle.setRented(false);

        vehicleRepo.save(vehicle);
        Rental updatedRental = rentalRepo.save(rental);

        return paymentService.createStripeCheckoutSession(updatedRental);
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

    //szukamy wolnych pojazdow za pomoca pola z klasy Vehicle isRented
    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return vehicleRepo.findById(vehicleId).map(Vehicle::isRented).orElse(false);
    }
}