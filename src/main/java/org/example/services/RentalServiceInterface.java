package org.example.services;

import com.stripe.exception.StripeException;
import org.example.models.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalServiceInterface {

    Rental rentVehicle(String userId, String vehicleId);

    String returnVehicle(String userId) throws StripeException;

    Optional<Rental> findActiveRentalByUserId(String userId);

    List<Rental> findAllRentals();

    List<Rental> findUserRentals(String userId);

    boolean userHasActiveRental(String userId);

    boolean vehicleHasActiveRental(String vehicleId);
}
