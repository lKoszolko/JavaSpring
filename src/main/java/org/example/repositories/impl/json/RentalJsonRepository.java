package org.example.repositories.impl.json;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalJsonRepository implements RentalRepository {
    private final List<Rental> rentals = new ArrayList<>();
    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals);
    }

    @Override
    public Optional<Rental> findById(String id) {
        return rentals.stream()
                .filter(rental -> rental.getId().equals(id))
                .findFirst();
    }

    @Override
    public Rental save(Rental rental) {
        Optional<Rental> existingRental = findById(rental.getId());
        existingRental.ifPresent(rentals::remove);
        rentals.add(rental);
        return rental;
    }

    @Override
    public void deleteById(String id) {
        rentals.removeIf(rental -> rental.getId().equals(id));
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.stream()
                .filter(rental -> rental.getId().equals(vehicleId)
                        && rental.getReturnDateTime() == null).findFirst();
    }
}
